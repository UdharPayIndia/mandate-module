package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextEnum
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextUiState
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.TimeConstant

@Dao
internal interface MandateSubtextDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(mandateSubtext: MandateSubtextEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(mandateSubtexts: List<MandateSubtextEntity>)

    @Query("SELECT * FROM mandate_subtext WHERE mandate_id = :mandateId AND mandate_status =:state AND subtext =:subtext")
    fun getOneNonLive(mandateId: String, state: String?, subtext: String?): MandateSubtextEntity?

    @Query("SELECT * FROM mandate_subtext WHERE mandate_id = :mandateId")
    fun getAllForMandateNonLive(mandateId: String): List<MandateSubtextEntity>?

    @Query("UPDATE mandate_subtext SET ui_state = :uiState WHERE mandate_id = :mandateId")
    fun updateUiState(mandateId: String, uiState: String)

    @Update
    fun updateAll(mandateSubtextEntities: List<MandateSubtextEntity>)

    @Update
    fun update(mandateSubtextEntities: MandateSubtextEntity)

    @Transaction
    fun upsert(
        mandateEntities: List<MandateEntity>
    ) {
        val mandateSubtexts = getUpdatedMandateSubtexts(mandateEntities)
        insertAll(mandateSubtexts.first)
        updateAll(mandateSubtexts.second)
    }

    private fun getUpdatedMandateSubtexts(
        mandateEntities: List<MandateEntity>
    ): Pair<List<MandateSubtextEntity>, List<MandateSubtextEntity>> {
        val updateList = mutableListOf<MandateSubtextEntity>()
        val insertList = mutableListOf<MandateSubtextEntity>()

        mandateEntities.toMutableList().forEach {
            val mandateSubtextEntity = getMandateSubTextEntity(it)
            val existingMandateSubtextEntity = getOneNonLive(it.id, it.state, mandateSubtextEntity.subtext)
            if (existingMandateSubtextEntity == null) {
                mandateSubtextEntity.subtextCreatedAt = System.currentTimeMillis()
                insertList.add(mandateSubtextEntity)
            } else {
                updateList.add(mandateSubtextEntity)
            }
        }
        return Pair(insertList, updateList)
    }

    private fun getMandateSubTextEntity(mandateEntity: MandateEntity): MandateSubtextEntity {
        val mandateSubtextEntity = MandateSubtextEntity()
        mandateSubtextEntity.mandateId = mandateEntity.id
        mandateSubtextEntity.mandateStatus = mandateEntity.state
        mandateSubtextEntity.uiState = if(mandateEntity.isUpdated){
            SubtextUiState.Unread.value
        }else{
            SubtextUiState.Read.value
        }
        val existingMandateSubText = getAllForMandateNonLive(mandateEntity.id)
        when(mandateEntity.state){
            MandateState.Pending.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.AskCustomerToAcceptSubText.value
                mandateSubtextEntity.priority = 0
                mandateSubtextEntity.uiState = SubtextUiState.Read.value
            }
            MandateState.UserAccepted.value -> {
                when{
                    shouldShowDateChangeDialog(mandateEntity.startAt)
                            && mandateEntity.isUpdated
                            && existingMandateSubText?.find { it.subtext == SubtextEnum.OneNewUpdateSubText.value}?.uiState != SubtextUiState.Read.value-> {
                        mandateSubtextEntity.subtext = SubtextEnum.OneNewUpdateSubText.value
                        mandateSubtextEntity.priority = 0
                    }
                    shouldShowDateAfterChangeDialog(mandateEntity.startAt)
                            && mandateEntity.isUpdated
                            && existingMandateSubText?.find { it.subtext == SubtextEnum.MandateAcceptedSubText.value}?.uiState != SubtextUiState.Read.value-> {
                        mandateSubtextEntity.subtext = SubtextEnum.MandateAcceptedSubText.value
                        mandateSubtextEntity.priority = 1
                    }
                    else -> {
                        mandateSubtextEntity.subtext = SubtextEnum.PaymentDueOnSubText.value
                        mandateSubtextEntity.priority = 2
                        mandateSubtextEntity.uiState = SubtextUiState.Read.value
                    }
                }
            }
            MandateState.Active.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.PaymentDueOnSubText.value
                mandateSubtextEntity.priority = 0
                mandateSubtextEntity.uiState = SubtextUiState.Read.value
            }
            MandateState.PartiallyCollected.value -> {
                when {
                    mandateEntity.endAt > System.currentTimeMillis()
                            && mandateEntity.isUpdated
                            && existingMandateSubText?.find { it.subtext == SubtextEnum.InstallmentCollectedSubText.value }?.uiState != SubtextUiState.Read.value -> {
                        mandateSubtextEntity.subtext = SubtextEnum.InstallmentCollectedSubText.value
                        mandateSubtextEntity.priority = 0
                    }
                    mandateEntity.endAt > System.currentTimeMillis() -> {
                        mandateSubtextEntity.subtext = SubtextEnum.RetryFailedInstallmentBefore.value
                        mandateSubtextEntity.priority = 1
                        mandateSubtextEntity.uiState = SubtextUiState.Read.value
                    }
                    else  -> {
                        mandateSubtextEntity.subtext = SubtextEnum.InstallmentCollectedSubText.value
                        mandateSubtextEntity.priority = 0
                        mandateSubtextEntity.uiState = SubtextUiState.Read.value
                    }
                }
            }
            MandateState.Completed.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.InstallmentCollectedSubText.value
                mandateSubtextEntity.priority = 0
            }
            MandateState.Paused.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.MandatePausedSubText.value
                mandateSubtextEntity.priority = 0
            }
            MandateState.Cancelled.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.MandateCancelledSubText.value
                mandateSubtextEntity.priority = 0
            }
            MandateState.Expired.value -> {
                mandateSubtextEntity.subtext = SubtextEnum.LinkExpiredCreateNewMandateSubText.value
                mandateSubtextEntity.priority = 0
                mandateSubtextEntity.uiState = SubtextUiState.Read.value
            }
            MandateState.Terminated.value -> {
                mandateSubtextEntity.subtext = null
                mandateSubtextEntity.priority = 0
            }
        }
        existingMandateSubText?.forEach {
            it.isSubTextDeleted = it.subtext != mandateSubtextEntity.subtext
        }
        if(!existingMandateSubText.isNullOrEmpty()) {
            updateAll(existingMandateSubText)
        }
        return mandateSubtextEntity
    }

    private fun shouldShowDateChangeDialog(startDate: Long): Boolean {
        return startDate < (System.currentTimeMillis() + (2 * TimeConstant.DAY))
    }

    private fun shouldShowDateAfterChangeDialog(startDate: Long): Boolean {
        return (startDate + TimeConstant.DAY) <= System.currentTimeMillis()
    }

}
