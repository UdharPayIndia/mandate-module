package com.rocketpay.mandate.feature.mandate.domain.usecase

import androidx.collection.arraySetOf
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.domain.repositories.InstallmentRepository
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeRequestDto
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.data.entities.MandateSummaryByState
import com.rocketpay.mandate.feature.mandate.data.entities.MetaData
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.domain.entities.CreateMandate
import com.rocketpay.mandate.feature.mandate.domain.entities.CustomerDetail
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateStateDto
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethodDetail
import com.rocketpay.mandate.feature.mandate.domain.entities.WhatsAppMessageConfig
import com.rocketpay.mandate.feature.mandate.domain.repositories.MandateRepository
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateFilter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateSearchFilterSort
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateSort
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class MandateUseCase internal constructor(
    private val mandateRepository: MandateRepository,
    private val dataValidator: DataValidator,
    private val installmentRepository: InstallmentRepository
) {

    internal suspend fun refreshMandate(mandateId: String): Outcome<Mandate>{
        val outcome = mandateRepository.refreshMandate(mandateId)
        updateMandateCalculation(mutableSetOf(mandateId))
        mandateRepository.saveMandatesSubtext(mutableSetOf(mandateId))
        return outcome
    }

    internal suspend fun computeCharges(amount: Double,
                                        frequency: String,
                                        installments: Int,
                                        bearer: String,
                                        paymentMethod: PaymentMethod,
                                        coupon: Coupon?,
                                        referenceId: String?,
                                        referenceType: String?): Outcome<ChargeResponseDto>{
        return mandateRepository.computeCharges(
            ChargeRequestDto(
                amount, frequency, installments, bearer, paymentMethod.value, coupon?.id,
                referenceId, referenceType
            )
        )
    }

    internal suspend fun getCouponList(amount: Double,
                                       frequency: String,
                                       installments: Int,
                                       bearer: String,
                                       paymentMethod: PaymentMethod): Outcome<List<Coupon>>{
        return mandateRepository.getCouponList(ChargeRequestDto(amount, frequency, installments, bearer, paymentMethod.value))

    }

    internal suspend fun createMandate(
        amount: Double,
        name: String,
        mobileNumber: String,
        paymentMethod: PaymentMethod,
        description: String,
        frequency: String,
        installments: Int,
        startDate: Long,
        upiId: String,
        product: String,
        bearer: String?,
        chargeId: String?,
        discountId: String?,
        amountWithoutCharges: Double?,
        originalAmount: Double,
        referenceId: String?,
        referenceType: String?,
        chargeResponseDto: ChargeResponseDto?,
    ): Outcome<Mandate> {
        val customer = CustomerDetail(name, mobileNumber)
        val paymentDetails = PaymentMethodDetail(paymentMethod, upiId)
        val mandateDom = CreateMandate(
            amount = amount,
            paymentMethodDetail = paymentDetails,
            installments = installments,
            startAt = startDate,
            frequency = frequency,
            description = description,
            customerDetail = customer,
            product = product,
            bearer = bearer,
            chargeId = chargeId,
            discountId = discountId,
            amountWithoutCharges = amountWithoutCharges,
            originalAmount = originalAmount,
            referenceId = referenceId,
            referenceType = referenceType,
            meta = MetaData(
                charges = chargeResponseDto,
                selfCheckoutDto = null
            )
        )
        return mandateRepository.createMandate(mandateDom)
    }

    fun getMandateSummaryByState(): Flow<List<MandateSummaryByState>> {
        return mandateRepository.getMandateSummaryByState()
    }

    fun getMandateSummaryByStateNonLive(): List<MandateSummaryByState>{
        return mandateRepository.getMandateSummaryByStateNonLive()
    }

    fun getMandateCount(): Int{
        return mandateRepository.getMandateCount()
    }

    internal fun getAllMandatesWithSubText(): Flow<List<Mandate>> {
        return mandateRepository.getAllMandatesWithSubText()
    }

    internal fun getMandate(mandateId: String): Flow<Mandate?> {
        return mandateRepository.getMandate(mandateId)
    }

    internal fun getMandateByReferenceId(referenceId: String): Flow<Mandate?>{
        return mandateRepository.getMandateByReferenceId(referenceId)
    }

    internal fun getMandateNonLive(mandateId: String): Mandate? {
        return mandateRepository.getMandateNonLive(mandateId)
    }

    fun isValidName(name: String): Boolean {
        return dataValidator.isValidName(name)
    }

    fun isValidModelName(name: String): Boolean {
        return dataValidator.isValidModelName(name)
    }

    fun isValidMobileNumber(number: String): Boolean {
        return dataValidator.isValidMobileNumber(number)
    }

    fun isValidImei(imei: String): Boolean {
        return dataValidator.isValidImei(imei)
    }


    fun isValidAmount(amount: String): Boolean {
        if (amount.isEmpty()) {
            return false
        }

        return try {
            dataValidator.isValidAmount(amount)
        } catch (e: Exception) {
            false
        }
    }

    fun isValidNotes(notes: String): Boolean {
        return dataValidator.isValidNotes(notes)
    }

    suspend fun sendPaymentRequest(mandateId: String): Outcome<GenericSuccessResponse> {
        return mandateRepository.sendPaymentRequest(mandateId)
    }

    suspend fun fetchMandateState(mandateId: String): Outcome<MandateStateDto> {
        return mandateRepository.fetchMandateState(mandateId)
    }

    internal suspend fun deleteMandate(mandateId: String): Outcome<Mandate> {
        return mandateRepository.deleteMandate(mandateId)
    }

    internal suspend fun cancelMandate(mandateId: String): Outcome<Mandate> {
        return mandateRepository.cancelMandate(mandateId)
    }

    internal suspend fun convertToManual(mandateId: String): Outcome<Mandate> {
        return mandateRepository.convertToManual(mandateId)
    }

    internal fun getFilteredMandate(
        mandates: List<Mandate>,
        mandateSearchFilterSort: MandateSearchFilterSort,
    ): List<Mandate> {
        return if (mandates.isEmpty()) {
            mandates
        } else {
            val filteredMandate = mandateSortFilter(mandates, mandateSearchFilterSort)
            mandateSearchSortFilter(filteredMandate, mandateSearchFilterSort)
        }
    }

    private fun mandateSortFilter(
        mandates: List<Mandate>,
        mandateSearchFilterSort: MandateSearchFilterSort,
    ): List<Mandate> {
        val filteredList = filterMandates(mandates, mandateSearchFilterSort.mandateFilter)
        return sortMandates(filteredList, mandateSearchFilterSort.mandateSort)
    }

    private fun filterMandates(
        mandates: List<Mandate>,
        mandateFilter: MandateFilter
    ): List<Mandate> {
        return when(mandateFilter) {
            MandateFilter.AllMandates -> mandates
            MandateFilter.Pending -> mandates.filter { it.state is MandateState.Pending }
            MandateFilter.Active -> mandates.filter { it.state is MandateState.Active || it.state is MandateState.UserAccepted || it.state is MandateState.PartiallyCollected }
            MandateFilter.Completed -> mandates.filter { it.state is MandateState.Completed }
            MandateFilter.Paused -> mandates.filter { it.state is MandateState.Paused }
            MandateFilter.Others -> mandates.filter { it.state is MandateState.Cancelled || it.state is MandateState.Terminated || it.state is MandateState.Expired }
        }
    }

    private fun sortMandates(filteredList: List<Mandate>, mandateSort: MandateSort): List<Mandate> {
        return when(mandateSort) {
            MandateSort.Newest -> filteredList.sortedByDescending { it.createdAt }
            MandateSort.HighestAmount -> filteredList.sortedByDescending { it.getMandateAmount() }
            MandateSort.Oldest -> filteredList.sortedBy { it.createdAt }
            MandateSort.LatestUpdate -> filteredList.sortedByDescending { it.updatedAt }
            MandateSort.NextInstallment -> filteredList.sortedBy { it.nextChargeAt }
        }
    }

    private fun mandateSearchSortFilter(
        mandates: List<Mandate>,
        mandateSearchFilterSort: MandateSearchFilterSort
    ): List<Mandate> {
        return mandates.filter {
            it.customerDetail.name.lowercase().contains(mandateSearchFilterSort.queryText.lowercase()) ||
                    it.customerDetail.mobileNumber.lowercase().contains(mandateSearchFilterSort.queryText.lowercase()) ||
                    it.getMandateAmount().toString().lowercase().contains(mandateSearchFilterSort.queryText.lowercase())
        }
    }

    internal suspend fun markMandateSubtextRead(mandate: Mandate) {
        return mandateRepository.markMandateSubtextRead(mandate)
    }

    fun getWhatsAppMessageConfig(): WhatsAppMessageConfig {
        return mandateRepository.getWhatsAppMessageConfig()
    }

    fun getCount(): Int {
        return mandateRepository.getCount()
    }

    suspend fun updateMandateCalculation(
        mandateIdList: MutableSet<String>,
    ) {
        withContext(Dispatchers.Default) {
            val updatedMandateIds = arraySetOf<String>()
            updatedMandateIds.addAll(mandateIdList)

            updatedMandateIds.forEach {
                val mandate = mandateRepository.getMandateNonLive(it)
                val installments = installmentRepository.getInstallmentsNonLive(it)
                mandate?.let { mandate ->
                    var amountRemaining = mandate.amount.double()
                    var installmentCollected = 0
                    var installmentAmountUI = 0.0
                    if(mandate.paymentMethodDetail.method == PaymentMethod.Manual){
                        installments?.forEach {
                            installmentAmountUI = it.getInstallmentAmount(mandate.bearer)
                            if(it.isMerchantCollected){
                                amountRemaining -= it.amount
                                installmentCollected += 1
                            }
                            installmentRepository.updateInstallment(it.id, installmentAmountUI)
                        }
                    }else{
                        installments?.forEach {
                            installmentAmountUI = it.getInstallmentAmount(mandate.bearer)
                            if(it.state in arrayOf(
                                    InstallmentState.CollectionSuccess,
                                    InstallmentState.SettlementInitiated,
                                    InstallmentState.SettlementFailed,
                                    InstallmentState.SettlementSuccess)
                                || it.isMerchantCollected
                            ){
                                amountRemaining -= it.amount
                                installmentCollected += 1
                            }
                            installmentRepository.updateInstallment(it.id, installmentAmountUI)
                        }
                    }
                    mandate.dueAmount = amountRemaining
                    mandate.installmentsPaid = installmentCollected
                    mandateRepository.updateMandate(mandate)
                }
            }
        }
    }
}
