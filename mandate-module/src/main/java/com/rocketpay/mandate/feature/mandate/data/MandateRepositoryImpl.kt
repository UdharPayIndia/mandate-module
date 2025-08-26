package com.rocketpay.mandate.feature.mandate.data

import androidx.sqlite.db.SimpleSQLiteQuery
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateDao
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateDataStore
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateSubtextDao
import com.rocketpay.mandate.feature.mandate.data.datasource.remote.MandateService
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeRequestDto
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateListResponse
import com.rocketpay.mandate.feature.mandate.data.entities.MandateSummaryByState
import com.rocketpay.mandate.feature.mandate.data.mapper.CreateMandateMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateDomToEntMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateDtoToEntMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateEntToDomMapper
import com.rocketpay.mandate.feature.mandate.data.mapper.MandateWithSubtextEntToDomMapper
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.domain.entities.CreateMandate
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateStateDto
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextUiState
import com.rocketpay.mandate.feature.mandate.domain.entities.WhatsAppMessageConfig
import com.rocketpay.mandate.feature.mandate.domain.repositories.MandateRepository
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.mandate.presentation.ui.utils.WhatsAppMessageParserUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

internal class MandateRepositoryImpl(
    private val mandateService: MandateService,
    private val mandateDao: MandateDao,
    private val mandateSubtextDao: MandateSubtextDao,
    private val createMandateMapper: CreateMandateMapper,
    private val mandateDtoToEntMapper: MandateDtoToEntMapper,
    private val mandateEntToDomMapper: MandateEntToDomMapper,
    private val mandateDomToEntMapper: MandateDomToEntMapper,
    private val mandateWithSubtextEntToDomMapper: MandateWithSubtextEntToDomMapper,
    private val mandateDataStore: MandateDataStore
): MandateRepository {

    override suspend fun computeCharges(chargeRequestDto: ChargeRequestDto): Outcome<ChargeResponseDto>{
        return mandateService.computeCharges(chargeRequestDto)
    }

    override suspend fun getCouponList(chargeRequestDto: ChargeRequestDto): Outcome<List<Coupon>>{
        val outcome = mandateService.getCouponList(chargeRequestDto)
        return when(outcome){
            is Outcome.Success -> {
                Outcome.Success(outcome.data.map {
                    Coupon(
                        id = it.id, name = it.name, description = it.description)
                })
            }

            is Outcome.Error -> {
                outcome
            }
        }
    }

    override suspend fun createMandate(mandateDom: CreateMandate): Outcome<Mandate> {
        return when(val outcome = mandateService.createMandate(createMandateMapper.map(mandateDom))) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                val mandateEntity = mandateDtoToEntMapper.map(outcome.data)
                Outcome.Success(mandateEntToDomMapper.map(mandateEntity))
            }
        }
    }

    override fun getMandateSummaryByState(): Flow<List<MandateSummaryByState>> {
        return mandateDao.getMandateSummaryByState()
    }

    override fun getMandateSummaryByStateNonLive(): List<MandateSummaryByState> {
        return mandateDao.getMandateSummaryByStateNonLive()
    }

    override fun getMandateCount(): Int{
        return mandateDao.getMandateCount()
    }

    override fun getAllMandatesWithSubText(): Flow<List<Mandate>>{
        val query = "SELECT mandate.*, mandate_subtext.* FROM mandate LEFT JOIN mandate_subtext ON (mandate.id = mandate_subtext.mandate_id AND mandate.is_deleted = 0 AND mandate_subtext.is_subtext_deleted = 0 AND mandate_subtext.priority = ( SELECT MIN(priority) FROM mandate_subtext WHERE mandate_id = mandate.id AND is_subtext_deleted = 0 AND mandate_status = mandate.status ) ) WHERE mandate.is_deleted = 0 AND method <> 'manual'"
        return mandateDao.getAllWithSubText(SimpleSQLiteQuery(query)).transform {
            emit(mandateWithSubtextEntToDomMapper.mapList(it))
        }
    }

    override fun getMandate(mandateId: String): Flow<Mandate?> {
        return mandateDao.getOne(mandateId).transform {
            emit(if (it == null) {
                null
            } else {
                mandateEntToDomMapper.map(it)
            })
        }
    }

    override fun getMandateByReferenceId(referenceId: String): Flow<Mandate?> {
        return mandateDao.getOneByReferenceId(referenceId).transform {
            emit(if (it == null) {
                null
            } else {
                mandateEntToDomMapper.map(it)
            })
        }
    }

    override fun getMandateNonLive(mandateId: String): Mandate? {
        val mandate = mandateDao.getOneNonLive(mandateId)
        return if (mandate == null) {
            null
        } else {
            mandateEntToDomMapper.map(mandate)
        }
    }

    override suspend fun syncMandates(createdAt: Long, updatedAt: Long): Outcome<MandateListResponse> {
        return mandateService.syncMandates(createdAt, updatedAt)
    }

    override suspend fun refreshMandate(mandateId: String): Outcome<Mandate>{
        return when(val outcome = mandateService.refreshMandate(mandateId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                val mandateEntity = mandateDtoToEntMapper.map(outcome.data)
                mandateDao.upsert(listOf(mandateEntity), {} , {_,_ ->})
                Outcome.Success(mandateEntToDomMapper.map(mandateEntity))
            }
        }
    }

    override fun saveMandates(
        data: List<MandateDto>,
        onInsert: (MandateEntity) -> Unit,
        onUpdate: (MandateEntity, MandateEntity) -> Unit
    ) {
        val mandateEntities = mandateDtoToEntMapper.mapList(data)
        mandateDao.upsert(mandateEntities, onInsert, onUpdate)
    }

    override suspend fun sendPaymentRequest(mandateId: String): Outcome<GenericSuccessResponse> {
        return mandateService.sendPaymentRequest(mandateId)
    }

    override suspend fun fetchMandateState(mandateId: String): Outcome<MandateStateDto> {
        return when(val outcome = mandateService.fetchMandateState(mandateId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                mandateDao.updateMandateState(mandateId, outcome.data.state)
                outcome
            }
        }
    }

    override suspend fun deleteMandate(mandateId: String): Outcome<Mandate> {
        return when(val outcome = mandateService.deleteMandate(mandateId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                mandateDao.deleteMandate(mandateId, true)
                val mandateEntity = mandateDtoToEntMapper.map(outcome.data)
                return Outcome.Success(mandateEntToDomMapper.map(mandateEntity))
            }
        }
    }

    override suspend fun cancelMandate(mandateId: String): Outcome<Mandate> {
        return when(val outcome = mandateService.cancelMandate(mandateId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                val mandateEntity = mandateDtoToEntMapper.map(outcome.data)
                return Outcome.Success(mandateEntToDomMapper.map(mandateEntity))
            }
        }
    }

    override suspend fun convertToManual(mandateId: String): Outcome<Mandate> {
        return when(val outcome = mandateService.convertToManual(mandateId)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                val mandateEntity = mandateDtoToEntMapper.map(outcome.data)
                return Outcome.Success(mandateEntToDomMapper.map(mandateEntity))
            }
        }
    }

    override suspend fun markMandateSubtextRead(mandate: Mandate){
        mandateSubtextDao.updateUiState(mandate.id, SubtextUiState.Read.value)
        mandateSubtextDao.upsert(listOf(mandateDomToEntMapper.map(mandate)))
    }

    override suspend fun saveMandatesSubtext(data: MutableSet<String>) {
        val mandates = mandateDao.getById(data.toMutableList()) ?: emptyList()
        withContext(Dispatchers.IO){ mandateSubtextDao.upsert(mandates) }
    }

    override fun getWhatsAppMessageConfig(): WhatsAppMessageConfig {
        return WhatsAppMessageParserUtils.getDefaultWhatsAppMessageConfig()
    }

    override fun getCount(): Int {
        return mandateDao.getCount()
    }

    override fun getMaxTimeStamp(): MandateEntity?{
        return mandateDao.lastTimeStamp()
    }

    override suspend fun updateMandate(mandate: Mandate){
        return mandateDao.update(mandateDomToEntMapper.map(mandate))
    }

}
