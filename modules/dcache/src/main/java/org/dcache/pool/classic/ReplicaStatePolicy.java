package org.dcache.pool.classic;

import java.util.List;

import diskCacheV111.vehicles.StorageInfo;

import org.dcache.pool.repository.EntryState;
import org.dcache.pool.repository.StickyRecord;

/**
 * A ReplicaStatePolicy defines the initial EntryState and
 * StickyRecords of a new replica when it is uploaded to a pool from a
 * client.
 */
public interface ReplicaStatePolicy
{

    List<StickyRecord> getStickyRecords(StorageInfo info);
    EntryState getTargetState(StorageInfo info);
}
