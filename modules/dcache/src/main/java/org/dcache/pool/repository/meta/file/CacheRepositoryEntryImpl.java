package org.dcache.pool.repository.meta.file;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.lang.ref.SoftReference;
import java.util.List;

import diskCacheV111.util.CacheException;
import diskCacheV111.util.PnfsId;
import diskCacheV111.vehicles.StorageInfo;

import org.dcache.pool.repository.EntryState;
import org.dcache.pool.repository.MetaDataRecord;
import org.dcache.pool.repository.StickyRecord;
import org.dcache.pool.repository.v3.RepositoryException;
import org.dcache.pool.repository.v3.entry.CacheRepositoryEntryState;

public class CacheRepositoryEntryImpl implements MetaDataRecord
{
    private final CacheRepositoryEntryState _state;
    private final PnfsId _pnfsId;
    private int _linkCount;
    private SoftReference<StorageInfo> _storageInfo;
    private long _creationTime = System.currentTimeMillis();
    private long _lastAccess;
    private long _size;

    /**
     * control file
     */
    private final File _controlFile;

    /**
     * serialized storage info file
     */
    private final File _siFile;

    /**
     * data file
     */
    private final File _dataFile;



    public CacheRepositoryEntryImpl(PnfsId pnfsId, File controlFile, File dataFile, File siFile ) throws IOException
    {

        _pnfsId = pnfsId;
        _controlFile = controlFile;
        _siFile = siFile;
        _dataFile = dataFile;

        _state = new CacheRepositoryEntryState(_controlFile);

        if (_siFile.exists()) {
            _creationTime = _siFile.lastModified();
        }

        _lastAccess = _dataFile.lastModified();
        _size = _dataFile.length();

        if (_lastAccess == 0) {
            _lastAccess = _creationTime;
        }
    }

    /**
     * Copy from existing MetaDataRecord.
     */
    public CacheRepositoryEntryImpl(PnfsId pnfsId, File controlFile,
                                    File dataFile,  File siFile,
                                    MetaDataRecord entry)
        throws IOException, RepositoryException, CacheException
    {
        _pnfsId = pnfsId;
        _controlFile = controlFile;
        _siFile = siFile;
        _dataFile = dataFile;
        _lastAccess   = entry.getLastAccessTime();
        _linkCount    = entry.getLinkCount();
        _creationTime = entry.getCreationTime();
        _size         = entry.getSize();
        _state        = new CacheRepositoryEntryState(_controlFile, entry);
        setStorageInfo(entry.getStorageInfo());
    }

    @Override
    public synchronized void incrementLinkCount()
    {
        _linkCount++;
    }

    @Override
    public synchronized void decrementLinkCount()
    {

        if (_linkCount <= 0) {
            throw new IllegalStateException("Link count is already  zero");
        }
        _linkCount--;
    }

    @Override
    public synchronized int getLinkCount() {
        return _linkCount;
    }

    @Override
    public synchronized long getCreationTime() {
        return _creationTime;
    }

    @Override
    public synchronized File getDataFile()
    {
        return _dataFile;
    }

    @Override
    public synchronized long getLastAccessTime() {
        return _lastAccess;
    }

    @Override
    public synchronized PnfsId getPnfsId() {
        return _pnfsId;
    }

    @Override
    public synchronized void setSize(long size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative entry size is not allowed");
        }
        _size = size;
    }

    @Override
    public synchronized long getSize() {
        return _size;
    }

    @Override
    public synchronized StorageInfo getStorageInfo()
    {
        StorageInfo si = null;
        if (_storageInfo != null) {
            si = _storageInfo.get();
        }
        if (si == null) {
            try {
                si = readStorageInfo(_siFile);
                _storageInfo = new SoftReference<>(si);
            }catch(FileNotFoundException fnf) {
                /* it's not an error
                 */
            } catch (IOException e) {
                throw new RuntimeException("Failed to read " + _siFile, e);
            }
        }
        return si;
    }

    @Override
    public synchronized boolean isSticky() {
        return _state.isSticky();
    }

    @Override
    public synchronized EntryState getState()
    {
        return _state.getState();
    }

    @Override
    public synchronized void setState(EntryState state)
        throws CacheException
    {
        try {
            _state.setState(state);
        } catch (IOException e) {
            throw new CacheException(e.getMessage());
        }
    }

    @Override
    public synchronized List<StickyRecord> removeExpiredStickyFlags()
    {
        return _state.removeExpiredStickyFlags();
    }

    @Override
    public synchronized boolean setSticky(String owner, long expire, boolean overwrite) throws CacheException {
        try {
            if (_state.setSticky(owner, expire, overwrite)) {
                return true;
            }
            return false;

        } catch (IllegalStateException | IOException e) {
            throw new CacheException(e.getMessage());
        }
    }
    @Override
    public synchronized void setStorageInfo(StorageInfo storageInfo) throws CacheException {

        ObjectOutputStream objectOut = null;
        File siFileTemp = null;
        try {

            siFileTemp = File.createTempFile(_siFile.getName(), null, _siFile.getParentFile() );

            objectOut = new ObjectOutputStream( new FileOutputStream( siFileTemp) );

            objectOut.writeObject( storageInfo ) ;


        } catch (IOException ioe) {
            // TODO: disk io error code here
            if( siFileTemp != null && siFileTemp.exists() ){
                siFileTemp.delete();
            }
            throw new CacheException(10,_pnfsId+" "+ioe.getMessage() );
        }finally {
            if( objectOut != null ) {
                try {
                    objectOut.close();
                } catch (IOException ignore) {
                }
            }
        }

        if( ! siFileTemp.renameTo(_siFile) ) {
            // TODO: disk io error code here
            throw new CacheException(10,_pnfsId+" rename failed" );
        }

        _storageInfo = new SoftReference<>(storageInfo);
    }

    @Override
    public synchronized void touch() throws CacheException {

        try{
            if( ! _dataFile.exists() ) {
                _dataFile.createNewFile();
            }
        }catch(IOException ee){
            throw new
                CacheException("Io Error creating : "+_dataFile ) ;
        }

        _lastAccess = System.currentTimeMillis();
        _dataFile.setLastModified(_lastAccess);
    }

    private static StorageInfo readStorageInfo(File objIn) throws IOException
    {
        try {
            ObjectInputStream in =
                new ObjectInputStream(new BufferedInputStream(new FileInputStream(objIn)));
            try {
                return (StorageInfo) in.readObject();
            } finally {
                try {
                    in.close();
                } catch (IOException we) {
                    // close on read can be ignored
                }
            }
        } catch (ClassNotFoundException cnf) {

        } catch (InvalidClassException ife) {
            // valid exception if siFIle is broken
        } catch( StreamCorruptedException sce ) {
            // valid exception if siFIle is broken
        } catch (OptionalDataException ode) {
            // valid exception if siFIle is broken
        } catch (EOFException eof){
            // object file size mismatch
        }
        return null;
    }

    @Override
    public synchronized List<StickyRecord> stickyRecords() {
        return _state.stickyRecords();
    }

    public synchronized String toString()
    {
        StorageInfo si = getStorageInfo();
        return _pnfsId.toString()+
            " <"+_state.toString()+"-"+
            "(0)"+
            "["+_linkCount+"]> "+
            getSize()+
            " si={"+(si==null?"<unknown>":si.getStorageClass())+"}" ;
    }
}
