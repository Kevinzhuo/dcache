package org.dcache.poolmanager;

import java.util.Collection;
import java.util.NoSuchElementException;

import diskCacheV111.poolManager.CostModule;
import diskCacheV111.poolManager.PoolSelectionUnit;
import diskCacheV111.util.FileLocality;
import diskCacheV111.vehicles.PoolCostCheckable;
import diskCacheV111.vehicles.PoolManagerPoolInformation;
import diskCacheV111.vehicles.ProtocolInfo;

import org.dcache.vehicles.FileAttributes;

/**
 * A PoolMonitor is the primary component for selecting pools.
 *
 * PoolSelectionUnit, CostModule and PartitionManager are collaborators of
 * PoolMonitor. PoolMonitor is however more than just a facade, as it provides
 * functionality not found in its collaborators.
 */
public interface PoolMonitor
{
    PoolSelectionUnit getPoolSelectionUnit();

    CostModule getCostModule();

    PartitionManager getPartitionManager();

    PoolSelector getPoolSelector(
            FileAttributes fileAttributes,
            ProtocolInfo protocolInfo,
            String linkGroup);

    Collection<PoolCostCheckable> queryPoolsByLinkName(
            String linkName, long filesize);

    PoolManagerPoolInformation getPoolInformation(String name)
            throws
            NoSuchElementException;

    Collection<PoolManagerPoolInformation> getPoolsByLink(
            String linkName)
            throws NoSuchElementException;

    Collection<PoolManagerPoolInformation> getPoolsByPoolGroup(
            String poolGroup)
            throws NoSuchElementException;

    FileLocality getFileLocality(FileAttributes attributes, String hostName);
}
