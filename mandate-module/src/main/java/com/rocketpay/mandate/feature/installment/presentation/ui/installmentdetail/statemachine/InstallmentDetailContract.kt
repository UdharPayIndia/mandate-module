package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentJourney
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.rocketpay.mandate.feature.settlements.domain.entities.PaymentOrder
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class InstallmentDetailState(
    val installmentId: String? = null,
    val referenceId: String? = null,
    val mandateId: String? = null,
    val installment: Installment? = null,
    val mandate: Mandate? = null,
    val isRefreshing: Boolean = false,
    val isExpanded: Boolean = false,
    val installmentPenalty: InstallmentPenalty? = null,
    val isManual: Boolean = false,
    val settlementBannerMessage: String = "",
    val paymentOrder: PaymentOrder? = null
) : BaseState(InstallmentDetailScreen)


internal sealed class InstallmentDetailEvent(name: String? = null) : BaseEvent(name) {
    data class LoadMandateAndInstallment(
        val mandateId: String?,
        val installmentId: String?,
        val referenceId: String?
    ) : InstallmentDetailEvent()
    data class FetchInstallment(val installmentId: String?): InstallmentDetailEvent()
    data class MandateLoaded(val mandate: Mandate?) : InstallmentDetailEvent()
    data class InstallmentLoaded(val installment: Installment?) : InstallmentDetailEvent()

    data class RefreshClick(val installmentId: String? = null): InstallmentDetailEvent()
    data class UnableToRefresh(val message: String): InstallmentDetailEvent()
    object DataRefreshed : InstallmentDetailEvent()

    object InstallmentJourneyClick: InstallmentDetailEvent()
    data class TransactionIdCopyClick(val message: String, val link: String): InstallmentDetailEvent("installment_txn_id_copy_click")
    data class RocketPayTransactionIdCopyClick(val message: String, val link: String): InstallmentDetailEvent("installment_rocket_pay_txn_id_copy_click")
    object ContactUsClick: InstallmentDetailEvent()

    object RetryInstallmentClick: InstallmentDetailEvent("installment_retry_click")

    object SkipInstallmentClick: InstallmentDetailEvent("installment_skip_click")
    object SkipInstallmentConfirmed: InstallmentDetailEvent("installment_skip_confirm_click")
    object SkipInstallmentDismiss: InstallmentDetailEvent()
    object InstallmentSkipped: InstallmentDetailEvent("installment_skipped")
    data class UnableToSkipInstallment(val message: String): InstallmentDetailEvent()

    data class UpdatePenaltyDetails(val installmentPenalty: InstallmentPenalty) : InstallmentDetailEvent()
    object ChargePenaltyClick: InstallmentDetailEvent("installment_charge_penalty_click")
    object MarkAsPaidClick: InstallmentDetailEvent("mark_paid_click")
    data object LoadSettlementBannerInfo: InstallmentDetailEvent()
    data class UpdateSettlementBannerMessage(val message: String): InstallmentDetailEvent()
    data object ViewSettlementClick: InstallmentDetailEvent("view_settlement_details")
    data object SettlementBannerClick: InstallmentDetailEvent("settlement_kyc_banner_click")
    data object AddBankAccountClick: InstallmentDetailEvent("settlement_bank_account_banner_click")
    data class UpdatePaymentOrder(val paymentOrder: PaymentOrder?): InstallmentDetailEvent()
    data object RefreshInstallment: InstallmentDetailEvent()
    data object DismissLoader: InstallmentDetailEvent()
    data class ActionFailed(val errorMessage: String): InstallmentDetailEvent()
    data object ActionSuccess: InstallmentDetailEvent()
}


internal sealed class InstallmentDetailASF : AsyncSideEffect {
    data class LoadMandate(val mandateId: String): InstallmentDetailASF()
    data class FetchInstallment(val installmentId: String): InstallmentDetailASF()
    data class LoadInstallment(val installmentId: String) : InstallmentDetailASF()
    data class RefreshData(val installmentId: String) : InstallmentDetailASF()
    data class SkipInstallment(val mandateId: String, val installmentId: String): InstallmentDetailASF()
    data class LoadPenalty(val installmentId: String): InstallmentDetailASF()
    data object LoadSettlementBannerInfo: InstallmentDetailASF()
    data class LoadPenaltyAndPaymentOrder(val installment: Installment?): InstallmentDetailASF()
    data class RefreshInstallment(val installmentId: String): InstallmentDetailASF()
}


internal sealed class InstallmentDetailUSF : UiSideEffect {
    data class UpdatePayments(val installmentJourney: List<InstallmentJourney>,
                              val isExpanded: Boolean,
                              val totalCount: Int,
                              val isManualMandate: Boolean,
                              val dueDate: Long,
                              val isMerchantCollected: Boolean): InstallmentDetailUSF()
    data class ShowToast(val message: String) : InstallmentDetailUSF()
    data class Copy(val message: String, val link: String) : InstallmentDetailUSF()
    object ContactUsClick: InstallmentDetailUSF()

    data class ShowSkipInstallmentConfirmation(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): InstallmentDetailUSF()
    data class SkipInstallmentInProgress(
        val title: String,
        val detail: String
    ): InstallmentDetailUSF()
    data class InstallmentSkipped(val title: String, val detail: String): InstallmentDetailUSF()
    object DismissSkipInstallmentConfirmation: InstallmentDetailUSF()
    data class UnableToSkipInstallment(
        val title: String,
        val detail: String
    ): InstallmentDetailUSF()
    data class OpenInstallmentUpdateScreen(
        val installmentId: String,
        val paymentMode: PaymentMode?,
        val comments: String?): InstallmentDetailUSF()
    data class OpenSettlementScreen(
        val paymentOrderId: String
    ): InstallmentDetailUSF()
    data object OpenKyc: InstallmentDetailUSF()
    data object OpenBankAccount: InstallmentDetailUSF()
    data class ShowLoader(val message: String): InstallmentDetailUSF()
    data class ShowError(val header: String, val message: String): InstallmentDetailUSF()
    data object DismissLoader: InstallmentDetailUSF()
    data class OpenEnterPenaltyBottomSheet(val mandateId: String, val installmentId: String, val installmentAmount: Double): InstallmentDetailUSF()
    data class OpenSelectRetryDateBottomSheet(val mandateId: String, val installmentId: String): InstallmentDetailUSF()
}

internal object InstallmentDetailScreen : Screen("installment_detail")
