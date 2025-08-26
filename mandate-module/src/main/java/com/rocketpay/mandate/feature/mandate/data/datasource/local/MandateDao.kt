package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.rocketpay.mandate.feature.mandate.data.entities.MandateSummaryByState
import kotlinx.coroutines.flow.Flow

@Dao
internal interface MandateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mandates: List<MandateEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(mandates: MandateEntity)

    @Query("SELECT * FROM mandate WHERE id = :mandateId")
    fun getOne(mandateId: String): Flow<MandateEntity?>

    @Query("SELECT * FROM mandate WHERE reference_id = :referenceId")
    fun getOneByReferenceId(referenceId: String): Flow<MandateEntity?>

    @Query("SELECT * FROM mandate WHERE id = :mandateId")
    fun getOneNonLive(mandateId: String): MandateEntity?

    @Query("SELECT * FROM mandate WHERE id in (:mandateIds)")
    fun getById(mandateIds: List<String>): List<MandateEntity>?

    @Query("SELECT * FROM mandate WHERE is_deleted = 0 AND method <> 'manual'")
    fun getAll(): Flow<List<MandateEntity>>

    @Query("SELECT * FROM mandate WHERE is_deleted = 0 AND method <> 'manual'")
    fun getAllNonLive(): List<MandateEntity>

    @Query("SELECT state, COUNT(*) as count FROM mandate WHERE is_deleted = 0 AND method <> 'manual' GROUP BY state")
    fun getMandateSummaryByState(): Flow<List<MandateSummaryByState>>

    @Query("SELECT state, COUNT(*) as count FROM mandate WHERE is_deleted = 0 AND method <> 'manual' GROUP BY state")
    fun getMandateSummaryByStateNonLive(): List<MandateSummaryByState>

    @Query("SELECT COUNT(*) FROM mandate WHERE is_deleted = 0 AND method <> 'manual'")
    fun getMandateCount(): Int

    @Query("UPDATE mandate SET state = :state WHERE id = :mandateId")
    fun updateMandateState(mandateId: String, state: String)

    @Query("UPDATE mandate SET is_deleted = :isDeleted WHERE id = :mandateId")
    fun deleteMandate(mandateId: String, isDeleted: Boolean)

    @RawQuery(observedEntities = arrayOf(MandateEntity::class, MandateSubtextEntity::class))
    fun getAllWithSubText(query: SimpleSQLiteQuery): Flow<List<MandateWithSubtextEntity>>

    @Update
    fun updateAll(mandateEntities: List<MandateEntity>)

    @Update
    fun update(mandateEntities: MandateEntity)

    @Query("SELECT * FROM mandate ORDER BY updated_at DESC, created_at DESC")
    fun lastTimeStamp(): MandateEntity

    @Transaction
    fun upsert(
        mandateEntities: List<MandateEntity>,
        onInsert: (MandateEntity) -> Unit,
        onUpdate: (MandateEntity, MandateEntity) -> Unit,
    ) {
        val mandates = getUpdatedMandates(mandateEntities, onInsert, onUpdate)
        insertAll(mandates.first)
        updateAll(mandates.second)
    }

    private fun getUpdatedMandates(
        mandateEntities: List<MandateEntity>,
        onInsert: (MandateEntity) -> Unit,
        onUpdate: (MandateEntity, MandateEntity) -> Unit,
    ): Pair<List<MandateEntity>, List<MandateEntity>> {
        val updateList = mutableListOf<MandateEntity>()
        val insertList = mutableListOf<MandateEntity>()

        mandateEntities.toMutableList().forEach {
            val mandateEntity = getOneNonLive(it.id)
            if (mandateEntity == null) {
                insertList.add(it)
                onInsert(it)
            } else {
                updateList.add(it)
                onUpdate(mandateEntity, it)
            }
        }

        return Pair(insertList, updateList)
    }

    @Query("SELECT COUNT(*) FROM mandate")
    fun getCount(): Int
}
