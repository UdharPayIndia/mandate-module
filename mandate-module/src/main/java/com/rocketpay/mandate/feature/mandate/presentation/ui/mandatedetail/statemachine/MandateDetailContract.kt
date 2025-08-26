package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.UpiApplication
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.EmptyUpiViewState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.TimeConstant
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class MandateDetailState(
    val mandateId: String? = null,
    val mandate: Mandate? = null,
    val installments: List<Installment> = emptyList(),
    val paymentViewState: MandateDetailViewState = MandateDetailViewState.Empty,
    val isRefreshing: Boolean = false,
    val isPolling: Boolean = false,
    val emptyUpiViewState: EmptyUpiViewState = EmptyUpiViewState.Before2Min,
    val pollInterval: Long = 10 * TimeConstant.SEC,
    val pollDuration: Long = 5 * TimeConstant.MINUTE,
    val linkExpireInterval: Long = 2 * TimeConstant.MINUTE,
    val upiApplication: UpiApplication = UpiApplication.None,
    val isMandateCreated: Boolean = false,
    val superKeyId: String? = null,
    var deleteEnable: Boolean = false,
    var cancelEnable: Boolean = false,
    val installmentSerialNumber: Int = -1,
    val isOtherDetailsExpanded: Boolean = false
) : BaseState(MandateDetailScreen)

sealed class MandateDetailViewState {
    object Filled : MandateDetailViewState()
    object Empty : MandateDetailViewState()
    object Retry : MandateDetailViewState()

}

internal sealed class MandateDetailEvent(name: String? = null) : BaseEvent(name) {
    data object Init: MandateDetailEvent()
    data class LoadMandate(val mandateId: String?, val installmentSerialNumber: Int) : MandateDetailEvent()
    object StartPolling: MandateDetailEvent()
    object StopPolling: MandateDetailEvent()
    data class RefreshMandate(val fromUser: Boolean = false): MandateDetailEvent()
    data class LoadSuperKey(val superKeyId: String): MandateDetailEvent()
    data class MandateLoaded(val mandate: Mandate?) : MandateDetailEvent()
    data class InstallmentsLoaded(val installments: List<Installment>, val nextInstallmentId: String?) : MandateDetailEvent()
    data class RefreshClick(val mandateId: String? = null, val isAutomatically: Boolean = false): MandateDetailEvent()
    data class UnableToRefresh(val message: String): MandateDetailEvent()
    data class DataRefreshed(val installments: List<Installment>? = null, val nextInstallmentId: String? = null) : MandateDetailEvent()
    object ShareOnWhatsAppClick: MandateDetailEvent()
    data class WhatsAppTemplateCreated(val mandate: Mandate, val experiment: String, val message: String): MandateDetailEvent()

    object CopyClick: MandateDetailEvent("copy_click")
    object ResendRequestClick: MandateDetailEvent("resend_request_click")
    data class UnableToSendPaymentRequest(val message: String): MandateDetailEvent()
    object PaymentRequestSent: MandateDetailEvent()
    object LoadMandateState: MandateDetailEvent()
    data class ChatWithUsClick(val progressDialogStatus: ProgressDialogStatus) : MandateDetailEvent()
    data class DateChangeDialogDismissClick(val progressDialogStatus: ProgressDialogStatus) : MandateDetailEvent()

    object GotoMandateList: MandateDetailEvent()

    object DeleteMandateClick: MandateDetailEvent("delete_mandate_click")
    object DeleteMandateConfirmClick: MandateDetailEvent("delete_mandate_confirmation_click")
    object DeleteMandateDismissClick: MandateDetailEvent()
    object MandateDeleted : MandateDetailEvent("mandate_deleted")
    data class UnableToDeleteMandate(val message: String) : MandateDetailEvent()

    object CancelMandateClick: MandateDetailEvent("cancel_mandate_click")
    object CancelMandateConfirmClick: MandateDetailEvent("cancel_mandate_confirmation_click")
    object CancelMandateDismissClick: MandateDetailEvent()
    object MandateCancelled : MandateDetailEvent("mandate_cancelled")
    data class UnableToCancelMandate(val message: String) : MandateDetailEvent()

    data class ItemClick(val installment: Installment): MandateDetailEvent("installment_click")

    object CreateNewInstallmentClick: MandateDetailEvent("create_installment_click")
    data class ShowErrorMessage(val errorMessage: String): MandateDetailEvent()
    data class LoadInstallments(val mandateId: String?, val stopPolling: Boolean): MandateDetailEvent()
    data object CallCustomerClick: MandateDetailEvent()
    object UpdateDeviceDetailsCardState: MandateDetailEvent()
    data object CloseScreen: MandateDetailEvent()
}


internal sealed class MandateDetailASF : AsyncSideEffect {
    data object InitConfig: MandateDetailASF()
    data class LoadMandate(val mandateId: String) : MandateDetailASF()
    data class LoadMandateByReferenceId(val referenceId: String): MandateDetailASF()
    data class CheckPollAndLoadInstallments(val mandateId: String?, val mandateState: MandateState) : MandateDetailASF()
    data class LoadInstallments(val mandateId: String?, val stopPolling: Boolean) : MandateDetailASF()
    data class RefreshData(val mandateId: String, val isAutomatically: Boolean) : MandateDetailASF()
    data class ResendRequest(val mandateId: String): MandateDetailASF()
    data class LoadMandateState(val mandateId: String): MandateDetailASF()
    data class DeleteMandate(val mandateId: String): MandateDetailASF()
    data class CancelMandate(val mandateId: String): MandateDetailASF()
    data class ShareOnWhatsAppClick(val mandate: Mandate): MandateDetailASF()
    data class StartPolling(val mandateId: String): MandateDetailASF()
    object StopPolling: MandateDetailASF()
    data class RefreshMandate(val mandateId: String): MandateDetailASF()
}


internal sealed class MandateDetailUSF : UiSideEffect {
    data class UpdateInstallments(
        val installments: List<Installment>,
        val nextInstallmentId: String?,
        val showMandateTag: Boolean,
        val manualMandate: Boolean,
        val installmentSerialNumber: Int = -1,
        val mandate: Mandate? = null
    ): MandateDetailUSF()
    data class ShowToast(val message: String) : MandateDetailUSF()
    data class ShareOnWhatsApp(val mobileNumber: String, val message: String): MandateDetailUSF()
    data class Copy(val paymentLink: String): MandateDetailUSF()
    data class UpdateHeader(val mandateState: MandateState, val mode: PaymentMethod, val pollInterval: Long, val pollDuration: Long): MandateDetailUSF()
    data class ShowDateChangeDialog(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): MandateDetailUSF()
    object DismissDateChangeDialog: MandateDetailUSF()
    object OpenChatWithUs: MandateDetailUSF()
    data class ShowDeleteMandateDialog(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): MandateDetailUSF()
    object DismissDeleteMandateDialog: MandateDetailUSF()
    data class ShowProgressDialog(val title: String, val detail: String): MandateDetailUSF()
    data object DismissProgressDialog: MandateDetailUSF()
    data class ShowErrorDialog(val title: String): MandateDetailUSF()
    data class DeleteMandateInProgress(val title: String, val detail: String): MandateDetailUSF()
    data class MandateDeletionFailed(val title: String, val detail: String): MandateDetailUSF()
    data class MandateDeleted(val title: String, val detail: String): MandateDetailUSF()
    data class ShowCancelMandateDialog(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): MandateDetailUSF()
    object DismissCancelMandateDialog: MandateDetailUSF()
    data class CancelMandateInProgress(val title: String, val detail: String): MandateDetailUSF()
    data class MandateCancelFailed(val title: String, val detail: String): MandateDetailUSF()
    data class MandateCancelled(val title: String, val detail: String): MandateDetailUSF()
    data class GotoMandateList(val message: String): MandateDetailUSF()
    data class GotoInstallmentDetail(val installmentId: String, val mandateId: String, val referenceId: String?): MandateDetailUSF()

    data class GotoCreateNewInstallmentClick(val mandateId: String): MandateDetailUSF()
    data class CallNumber(val mobileNumber: String): MandateDetailUSF()
    data object CloseScreen: MandateDetailUSF()
}

internal object MandateDetailScreen : Screen("mandate_detail")
