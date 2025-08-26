package com.rocketpay.mandate.feature.mandate.domain.repositories

import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeRequestDto
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateListResponse
import com.rocketpay.mandate.feature.mandate.data.entities.MandateSummaryByState
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.domain.entities.CreateMandate
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateStateDto
import com.rocketpay.mandate.feature.mandate.domain.entities.WhatsAppMessageConfig
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal interface MandateRepository {
    suspend fun computeCharges(chargeRequestDto: ChargeRequestDto): Outcome<ChargeResponseDto>
    suspend fun getCouponList(chargeRequestDto: ChargeRequestDto): Outcome<List<Coupon>>
    suspend fun createMandate(mandateDom: CreateMandate): Outcome<Mandate>
    suspend fun deleteMandate(mandateId: String): Outcome<Mandate>
    suspend fun cancelMandate(mandateId: String): Outcome<Mandate>
    suspend fun convertToManual(mandateId: String): Outcome<Mandate>


    fun getMandateSummaryByState(): Flow<List<MandateSummaryByState>>
    fun getMandateSummaryByStateNonLive(): List<MandateSummaryByState>
    fun getMandateCount(): Int
    fun getAllMandatesWithSubText(): Flow<List<Mandate>>
    fun getMandate(mandateId: String): Flow<Mandate?>
    fun getMandateByReferenceId(referenceId: String): Flow<Mandate?>

    suspend fun sendPaymentRequest(mandateId: String): Outcome<GenericSuccessResponse>
    suspend fun fetchMandateState(mandateId: String): Outcome<MandateStateDto>
    fun getMandateNonLive(mandateId: String): Mandate?

    suspend fun syncMandates(createdAt: Long, updatedAt: Long): Outcome<MandateListResponse>
    suspend fun refreshMandate(mandateId: String): Outcome<Mandate>
    fun saveMandates(data: List<MandateDto>, onInsert: (MandateEntity) -> Unit,
                     onUpdate: (MandateEntity, MandateEntity) -> Unit)

    suspend fun markMandateSubtextRead(mandate: Mandate)
    suspend fun saveMandatesSubtext(data: MutableSet<String>)
    fun getWhatsAppMessageConfig(): WhatsAppMessageConfig
    fun getCount(): Int
    fun getMaxTimeStamp(): MandateEntity?
    suspend fun updateMandate(mandate: Mandate)
}
