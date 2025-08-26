package com.rocketpay.mandate.common.syncmanager.client.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.udharpay.core.syncmanager.domain.enities.SyncTypeVsStatus
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SyncRequestDao {

    @Insert
    fun insertAll(syncRequests: List<SyncRequestEntity>)

    @Query("SELECT * FROM sync_request WHERE status = :status AND back_off_time < :backOffTime")
    fun getAll(status: String, backOffTime: Long): List<SyncRequestEntity>

    @Query("SELECT * FROM sync_request WHERE type = :type")
    fun getOne(type: String): List<SyncRequestEntity>

    @Query("SELECT COUNT(*) FROM sync_request WHERE status = :status AND back_off_time < :backOffTime")
    fun getCount(status: String, backOffTime: Long): Long

    @Update
    fun update(requests: List<SyncRequestEntity>)

    @Query("DELETE FROM sync_request WHERE status = :syncStatus")
    fun removeSuccessRequest(syncStatus: String)

    @Query("DELETE FROM sync_request WHERE status = :syncStatus AND retry_count <= 0")
    fun removeFailedRequest(syncStatus: String)

    @Query("UPDATE sync_request SET status = :destination WHERE status = :source AND retry_count > 0")
    fun replaceFailedWithEnqueue(source: String, destination: String)

    @Query("UPDATE sync_request SET status = :destination WHERE status = :source")
    fun replace(source: String, destination: String)

    @Query("DELETE FROM sync_request WHERE id NOT IN ( SELECT MIN(id) FROM sync_request GROUP BY type ) ;")
    fun removeDuplicate()

    @Query("SELECT status FROM sync_request WHERE type = :type")
    fun getSyncStatus(type: String): Flow<String>

    @Query("SELECT type AS syncType, status AS syncStatus FROM sync_request WHERE :where")
    fun getSyncStatuses(where: String): Flow<List<SyncTypeVsStatus>>
}
