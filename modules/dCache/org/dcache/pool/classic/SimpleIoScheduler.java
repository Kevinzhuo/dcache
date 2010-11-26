package org.dcache.pool.classic;

import diskCacheV111.util.CacheException;
import diskCacheV111.util.JobScheduler.Job;
import diskCacheV111.vehicles.IoJobInfo;
import diskCacheV111.vehicles.JobInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import org.dcache.pool.FaultAction;
import org.dcache.pool.FaultEvent;
import org.dcache.util.AdjustableSemaphore;
import org.dcache.util.FifoPriorityComparator;
import org.dcache.util.IoPrioritizable;
import org.dcache.util.IoPriority;
import org.dcache.util.LifoPriorityComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @since 1.9.11
 */
public class SimpleIoScheduler implements IoScheduler, Runnable {

    private final static Logger _log =
            LoggerFactory.getLogger(SimpleIoScheduler.class);

    /**
     * A worker thread for request queue processing.
     */
    private final Thread _worker;

    /**
     * The name of IoScheduler.
     */
    private final String _name;

    /**
     * request queue.
     */
    private final BlockingQueue<PrioritizedRequest> _queue;

    private final Map<Integer, PrioritizedRequest> _jobs = new ConcurrentHashMap<Integer, PrioritizedRequest>();

    /**
     * ID of the current queue. Used to identify queue in {@link IoQueueManager}.
     */
    private final int _queueId;

    /**
     * job id generator
     */
    private int _nextId = 0;

    /**
     * are we need to shutdown.
     */
    boolean _shutdown = false;

    private final AdjustableSemaphore _semaphore = new AdjustableSemaphore();

    private final MoverExecutorServices _executorServices;

    public SimpleIoScheduler(String name, MoverExecutorServices executorServices, int queueId) {
        this(Executors.defaultThreadFactory(), name, executorServices, queueId);
    }

    public SimpleIoScheduler(String name, MoverExecutorServices executorServices, int queueId, boolean fifo) {
        this(Executors.defaultThreadFactory(), name, executorServices, queueId, fifo);
    }

    public SimpleIoScheduler(ThreadFactory factory, String name, MoverExecutorServices executorServices, int queueId) {
        this(factory, name, executorServices, queueId, true);
    }

    public SimpleIoScheduler(ThreadFactory factory, String name, MoverExecutorServices executorServices, int queueId, boolean fifo)
    {
        _name = name;
        _executorServices = executorServices;
        _queueId = queueId;
        _queue = new PriorityBlockingQueue<PrioritizedRequest>(16, fifo? new FifoPriorityComparator() :
            new LifoPriorityComparator());

        _semaphore.setMaxPermits(2);
        _worker = factory.newThread(this);
        _worker.setName(_name);
        _worker.start();
    }

    /**
     * Add a request into the queue. The returned id is composed from queue id
     * and internal counter:
     *   | 31- queue id -24|23- job id -0|
     *
     * @param request
     * @param priority
     * @return mover id
     */
    @Override
    public  int add(PoolIORequest request, IoPriority priority) {

        int id = _queueId << 24 | nextId();

        if (_semaphore.getMaxPermits() <= 0) {
            _log.warn("A task was added to queue '{}', however the queue is not configured to execute any tasks.", _name);
        }

        PrioritizedRequest wrapper = new PrioritizedRequest(id, request, priority);
        _queue.add(wrapper);
        _jobs.put(id, wrapper);
        request.setState(IoRequestState.QUEUED);

        return id;
    }

    private synchronized int nextId() {
        if(_nextId == 0x00FFFFFF) {
            _nextId = 0;
        }else{
            _nextId++;
        }
        return _nextId;
    }

    @Override
    public int getActiveJobs() {
        return _semaphore.getUsedPermits();
    }

    @Override
    public PoolIORequest getJobInfo(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JobInfo> getJobInfos() {
        List<JobInfo> jobs = new ArrayList<JobInfo>();

        for (Map.Entry<Integer, PrioritizedRequest> job : _jobs.entrySet()) {
            jobs.add(toJobInfo(job.getValue().getRequest(), job.getKey()));
        }

        /*
         * return unmodifiable list to 'kill' every one who wants to
         * change it (in other words, for bug tracking).
         */
        return Collections.unmodifiableList(jobs);
    }

    @Override
    public int getMaxActiveJobs() {
        return _semaphore.getMaxPermits();
    }

    @Override
    public int getQueueSize() {
        return _queue.size();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void cancel(int id) throws NoSuchElementException {

        PrioritizedRequest wrapper;
        wrapper = _jobs.remove(id);

        if (wrapper == null) {
            throw new NoSuchElementException("Job " + id + " not found");
        }
        _queue.remove(wrapper);
        wrapper.getRequest().kill();
    }

    @Override
    public StringBuffer printJobQueue(StringBuffer sb) {
        for (Map.Entry<Integer, PrioritizedRequest> job : _jobs.entrySet()) {
            sb.append(job.getKey()).append(" : ").append(job.getValue().getRequest()).append('\n');
        }
        return sb;
    }

    @Override
    public void setMaxActiveJobs(int maxJobs) {
        _semaphore.setMaxPermits(maxJobs);
    }

    @Override
    public void shutdown() {
        // NOP for now
    }

    @Override
    public void run() {
            while (!_shutdown) {

                try {
                    final PrioritizedRequest wrapp = _queue.take();
                    _semaphore.acquire();

                    final PoolIORequest request = wrapp.getRequest();
                    final String protocolName = request.getProtocolInfo().getProtocol() + "-"
                        + request.getProtocolInfo().getMajorVersion();
                        request.transfer(_executorServices.getExecutorService(protocolName),
                        new CompletionHandler() {

                        @Override
                        public void completed(Object result, Object attachment) {
                            postProcess(0, "");
                        }

                        @Override
                        public void failed(Throwable e, Object attachment) {

                            int rc;
                            String msg;
                            if (e instanceof InterruptedException) {
                                rc = CacheException.DEFAULT_ERROR_CODE;
                                msg = "Transfer was killed";
                            } else if (e instanceof CacheException) {
                                rc = ((CacheException) e).getRc();
                                msg = e.getMessage();
                                if (rc == CacheException.ERROR_IO_DISK) {
                                    request.getFaultListener().faultOccurred(new FaultEvent("repository", FaultAction.DISABLED, msg, e));
                                }
                            } else {
                                rc = CacheException.UNEXPECTED_SYSTEM_EXCEPTION;
                                msg = "Transfer failed due to unexpected exception: " + e;
                            }
                            postProcess(rc, msg);
                        }

                        private void postProcess(int rc, String msg) {
                            _semaphore.release();
                            _jobs.remove(wrapp.getId());
                            request.setState(IoRequestState.DONE);
                            request.sendBillingMessage(rc, msg);
                            _executorServices.getPostExecutorService(protocolName).execute(request);
                        }
                    });
                }catch(InterruptedException e) {
                    _shutdown = true;
                }
            }
    }

    /*
     * wrapper for priority queue
     */
    private static class PrioritizedRequest implements IoPrioritizable {

        private final PoolIORequest _request;
        private final IoPriority _priority;
        private final long _ctime;
        private final int _id;

        PrioritizedRequest(int id, PoolIORequest o, IoPriority p) {
            _id = id;
            _request = o;
            _priority = p;
            _ctime = System.nanoTime();
        }

        public PoolIORequest getRequest() {
            return _request;
        }

        public int getId() {
            return _id;
        }

        @Override
        public IoPriority getPriority() {
            return _priority;
        }

        @Override
        public long getCreateTime() {
            return _ctime;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PrioritizedRequest)) {
                return false;
            }

            final PrioritizedRequest other = (PrioritizedRequest) o;
            return _id == other._id;
        }

        @Override
        public int hashCode() {
            return _id;
        }
    }

    private static JobInfo toJobInfo(final PoolIORequest request, final int id) {

        IoJobInfo jobInfo = new IoJobInfo(new Job() {

            @Override
            public long getSubmitTime() {
                return request.getCreationTime();
            }

            @Override
            public long getStartTime() {
                return request.getStartTime();
            }

            @Override
            public String getStatusString() {
                return request.getState().toString();
            }

            @Override
            public Runnable getTarget() {
                return null;
            }

            @Override
            public int getJobId() {
                return id;
            }
        });

        jobInfo.setIoInfo(request.getPnfsId(), request.getBytesTransferred(),
                request.getTransferTime(), request.getLastTransferred());
        jobInfo.setClient(request.getClient(), request.getClientId());
        return jobInfo;
    }
}
