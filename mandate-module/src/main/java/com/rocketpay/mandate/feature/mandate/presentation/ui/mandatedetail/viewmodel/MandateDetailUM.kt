package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailViewState
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.underline
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.PhoneUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.QrGeneratorUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.vm.ItemBadgeVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class MandateDetailUM(val dispatchEvent: (MandateDetailEvent) -> Unit) : BaseMainUM() {

    private var qrData: String? = null
    private var qrBitmap: Bitmap? = null

    val itemBadge = ItemBadgeVM()
    val amount = ObservableField<String>()
    val amountColor = ObservableInt()

    val orderNote = ObservableField<String>()
    val paymentMode = ObservableField<String>()
    val creationTime = ObservableField<String>()

    val installmentInfo = ObservableField<String>()

    val qrVisibility = ObservableBoolean()
    val qrBitmapObservable = ObservableField<Bitmap>()

    val isOtherDetailsExpanded = ObservableBoolean()

    //////////////// Empty layout only ///////////////////////

    val emptyStateVisibility = ObservableInt()

    val retryMessageVisibility = ObservableInt()
    val retryMessageText = ObservableField<String>()

    val headerTitle = ObservableField<String>()
    val headerSubtitle = ObservableField<SpannableString>()
    val headerMessage = ObservableField<String>()

    val primaryCtaEnable = ObservableBoolean(false)
    val primaryCtaText = ObservableField<String>()
    val primaryCtaIcon = ObservableField<Drawable>()
    val primaryCtaBackground = ObservableField<Drawable>()
    val primaryCtaColor = ObservableInt()

    val isDeleteEnabled = MutableLiveData<Boolean>()
    val isCancelEnabled = MutableLiveData<Boolean>()

    val dateChangeDialogVM = ProgressDialogVM (
        { dispatchEvent(MandateDetailEvent.DateChangeDialogDismissClick(it)) },
        { dispatchEvent(MandateDetailEvent.ChatWithUsClick(it)) },
    )

    val mandateDeleteConfirmationDialogVM = ProgressDialogVM(
        {
            when(it) {
                ProgressDialogStatus.Init -> dispatchEvent(MandateDetailEvent.DeleteMandateConfirmClick)
                ProgressDialogStatus.Progress -> {
                    // No action required
                }
                ProgressDialogStatus.Success -> dispatchEvent(MandateDetailEvent.GotoMandateList)
                ProgressDialogStatus.Error -> dispatchEvent(MandateDetailEvent.DeleteMandateDismissClick)
            }
        },
        { dispatchEvent(MandateDetailEvent.DeleteMandateDismissClick) }
    )

    val progressDialogVM = ProgressDialogVM(
        {
            dispatchEvent(MandateDetailEvent.CloseScreen)
        },
        {

        }
    )

    val mandateCancelConfirmationDialogVM = ProgressDialogVM(
        {
            when(it) {
                ProgressDialogStatus.Init -> dispatchEvent(MandateDetailEvent.CancelMandateConfirmClick)
                ProgressDialogStatus.Progress -> {
                    // No action required
                }
                ProgressDialogStatus.Success -> dispatchEvent(MandateDetailEvent.CancelMandateDismissClick)
                ProgressDialogStatus.Error -> dispatchEvent(MandateDetailEvent.CancelMandateDismissClick)
            }
        },
        { dispatchEvent(MandateDetailEvent.CancelMandateDismissClick) }
    )

    fun onPrimaryCtaClick() {
        dispatchEvent(MandateDetailEvent.ShareOnWhatsAppClick)
    }

    fun onCopyClick() {
        dispatchEvent(MandateDetailEvent.CopyClick)
    }

    fun onCreateNewInstallmentClick() {
        dispatchEvent(MandateDetailEvent.CreateNewInstallmentClick)
    }

    fun handleState(state: MandateDetailState) {
        updateMandateDetails(state)
    }

    private fun updateMandateDetails(state: MandateDetailState) {
        isOtherDetailsExpanded.set(state.isOtherDetailsExpanded)
        isDeleteEnabled.postValue(state.deleteEnable)
        isCancelEnabled.postValue(state.cancelEnable)
        toolbarTitleString.set(state.mandate?.customerDetail?.name)
        val mobileNumberWithoutCountryCode = PhoneUtils.removedCountryCodeFromMobileNumber(state.mandate?.customerDetail?.mobileNumber ?: "")
        toolbarSubtitleString.set(mobileNumberWithoutCountryCode)
        updateNonMandateKhataDetails(state)
        updatePaymentHistory(state)
        updateMandateKhataDetails(state)
    }

    private fun updateNonMandateKhataDetails(state: MandateDetailState) {
        if (state.mandate == null) return

        val mandateStateMeta = MandateStateUi.getMandateStateUi(state.mandate.state)
        itemBadge.text.set(ResourceManager.getInstance().getString(R.string.rp_mandate_detail_amount_status, ResourceManager.getInstance().getString(mandateStateMeta.text)))
        itemBadge.textColor.set(ResourceManager.getInstance().getColor(mandateStateMeta.color))

        val tempAmountColor = when(mandateStateMeta) {
            MandateStateUi.Pending, MandateStateUi.UserAccepted, MandateStateUi.Active, MandateStateUi.Completed, MandateStateUi.Paused -> ResourceManager.getInstance().getColor(R.color.rp_grey_1)
            MandateStateUi.Cancelled, MandateStateUi.Expired, MandateStateUi.Terminated -> ResourceManager.getInstance().getColor(R.color.rp_grey_3)
        }
        amountColor.set(tempAmountColor)

        orderNote.set(state.mandate.description)
        when (state.mandate.paymentMethodDetail.method) {
            PaymentMethod.Nach -> {
                paymentMode.set(state.mandate.paymentMethodDetail.method.value.uppercase())
            }
            PaymentMethod.Upi -> {
                val paymentModeText = if(!state.mandate.paymentMethodDetail.upiId.isNullOrEmpty()){
                    "${state.mandate.paymentMethodDetail.method.value.uppercase()}(${state.mandate.paymentMethodDetail.upiId})"
                }else{
                    state.mandate.paymentMethodDetail.method.value.uppercase()
                }
                paymentMode.set(paymentModeText)
            }
            else ->{

            }
        }
        val createdAt = DateUtils.getDate(state.mandate.createdAt, DateUtils.MONTH_DATE_FORMAT)
        creationTime.set(ResourceManager.getInstance().getString(R.string.rp_mandate_created_on, createdAt))
    }

    private fun updatePaymentHistory(state: MandateDetailState) = when(state.paymentViewState) {
        MandateDetailViewState.Empty -> {
            // TODO: psp integration
            emptyStateVisibility.set(View.VISIBLE)
            retryMessageVisibility.set(View.GONE)

            headerTitle.set(ResourceManager.getInstance().getString(R.string.rp_share_payment_link_authorise))
            if(!state.mandate?.mandateUrl.isNullOrEmpty()){
                headerSubtitle.set(state.mandate?.mandateUrl?.getSpannable()?.underline(state.mandate?.mandateUrl.orEmpty()))
            }

            val updatedQrData = state.mandate?.meta?.selfCheckoutDto?.qr?.authData?.qrData
            if(!updatedQrData.isNullOrEmpty()){
                qrVisibility.set(true)
                if(qrData != updatedQrData){
                    this.qrData = updatedQrData
                    qrBitmap = QrGeneratorUtils.generateQrCodeFromUrl(qrData.toString())
                    qrBitmapObservable.set(qrBitmap)
                }
            }else{
                qrVisibility.set(false)
            }

            primaryCtaEnable.set(true)
            primaryCtaText.set(ResourceManager.getInstance().getString(R.string.rp_share_on_whatsapp))
            primaryCtaIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_whatsapp, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
            primaryCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta_small))
            primaryCtaColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
        }
        MandateDetailViewState.Retry -> {
            emptyStateVisibility.set(View.GONE)
            retryMessageVisibility.set(View.VISIBLE)
            val endDate = DateUtils.getDate(state.mandate?.endAt ?: 0L, DateUtils.MONTH_DATE_FORMAT)
            retryMessageText.set(ResourceManager.getInstance().getString(R.string.rp_retry_failed_installment_before, endDate))
        }
        MandateDetailViewState.Filled -> {
            emptyStateVisibility.set(View.GONE)
            retryMessageVisibility.set(View.GONE)
        }
    }

    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////

    val installmentListVisibility = ObservableInt()
    val addInstallmentVisibility = ObservableInt()

    private fun updateMandateKhataDetails(state: MandateDetailState) {
        state.mandate?.let {
            val amountValue = state.mandate.getMandateAmount()
            val amountPaidValue = state.mandate.getMandatePaidAmount()
            val amountDueValue = state.mandate.getMandateDueAmount()
            if (state.mandate.product is MandateProduct.Khata) {
                val totalAmount = AmountUtils.format(amountValue)
                val amountPaid = AmountUtils.format(amountPaidValue)
                if (amountPaidValue <= 0) {
                    amount.set(totalAmount)
                } else {
                    amount.set(ResourceManager.getInstance().getString(R.string.rp_mandate_detail_khata_amount_paid_due, totalAmount, amountPaid))
                }

                installmentInfo.set("")

                if (state.installments.isEmpty()) {
                    installmentListVisibility.set(View.GONE)
                } else {
                    installmentListVisibility.set(View.VISIBLE)
                }

                if (state.mandate.frequency is InstallmentFrequency.Adhoc && state.mandate.state is MandateState.Active) {
                    addInstallmentVisibility.set(View.VISIBLE)
                } else {
                    addInstallmentVisibility.set(View.GONE)
                }
            } else {
                val amountPaid = AmountUtils.format(amountPaidValue)
                val amountDue = AmountUtils.format(amountDueValue)
                if (amountDueValue <= 0) {
                    amount.set(ResourceManager.getInstance().getString(R.string.rp_mandate_detail_amount_paid, amountPaid))
                } else if (amountPaidValue <= 0) {
                    amount.set(ResourceManager.getInstance().getString(R.string.rp_mandate_detail_amount_due, amountDue))
                } else {
                    amount.set(ResourceManager.getInstance().getString(R.string.rp_mandate_detail_amount_paid_due, amountPaid, amountDue))
                }

                val totalAmount = AmountUtils.format(amountValue)
                val perInstallmentAmount = AmountUtils.format(state.mandate.getMandateInstallmentAmount())
                val installmentPerText = ResourceManager.getInstance().getString(state.mandate.frequency.suffix_s)
                val totalNumberOfInstallment = state.mandate.installments
                installmentInfo.set(ResourceManager.getInstance().getString(R.string.rp_payment_installment_info, perInstallmentAmount, totalNumberOfInstallment, installmentPerText, totalAmount))

                installmentListVisibility.set(View.VISIBLE)
                addInstallmentVisibility.set(View.GONE)
            }
        }
    }

    fun onChangeOtherDetailState(){
        dispatchEvent(MandateDetailEvent.UpdateDeviceDetailsCardState)
    }
}
