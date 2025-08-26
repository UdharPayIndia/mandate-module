package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.common.domain.CommonUseCase

internal class InstallmentDetailUM(val dispatchEvent: (InstallmentDetailEvent) -> Unit) : BaseMainUM() {

    val isManualPayment = ObservableBoolean()
    val isManuallyCollected = ObservableBoolean()
    val stateColor = ObservableInt()
    val amount = ObservableField<String>()
    val fromParty = ObservableField<String>()
    val installmentUtrLabel = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_app_transaction_id,
        BuildConfig.APP_NAME))
    val installmentUtr = ObservableField<String>()
    val fromDetail = ObservableField<String>()
    val toDetail = ObservableField<String>()
    val utr = ObservableField<String>()
    val utrCopyVisibility = ObservableInt()

    val retryVisibility = ObservableBoolean()
    val retryEnable = ObservableBoolean()
    val retryCtaBackground = ObservableField<Drawable>()
    val retryTextColor = ObservableInt()
    val retryIcon = ObservableField<Drawable>()

    val skipVisibility = ObservableBoolean()
    val skipEnable = ObservableBoolean()
    val skipTextColor = ObservableInt()
    val skipCtaBackground = ObservableField<Drawable>()
    val skipIcon = ObservableField<Drawable>()

    val penaltyVisibility = ObservableBoolean()
    val penaltyEnable = ObservableBoolean()
    val penaltyTextColor = ObservableInt()
    val penaltyCtaBackground = ObservableField<Drawable>()
    val penaltyIcon = ObservableField<Drawable>()

    val markAsPaidVisibility = ObservableBoolean()
    val markAsPaidEnable = ObservableBoolean()
    val markAsPaidTextColor = ObservableInt()
    val markAsPaidCtaBackground = ObservableField<Drawable>()
    val markAsPaidIcon = ObservableField<Drawable>()

    val chargeVisibility = ObservableBoolean()
    val totalCollectedAmount = ObservableField<String>()
    val chargeLabelText = ObservableField<String>()
    val chargeAmount = ObservableField<String>()
    val userCollectedAmount = ObservableField<String>()

    val settlementBannerMessage = ObservableField<String>()
    val settlementBannerClickable = ObservableBoolean()

    val settlementButtonVisibility = ObservableBoolean()

    val penaltyDetailVisibility = ObservableBoolean(false)
    val penaltyTitleText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_app_bounce_penalty_collected,
        BuildConfig.APP_NAME))
    val penaltyDetailText = ObservableField<String>()

    val manualSummaryVisibility = ObservableBoolean(false)
    val time = ObservableField<String>()
    val mode = ObservableField<String>()
    val comment = ObservableField<String>()

    val refundMessageVisibility = ObservableBoolean()

    val skipInstallmentConfirmationDialogVM = ProgressDialogVM(
        {
            when(it) {
                ProgressDialogStatus.Init -> dispatchEvent(InstallmentDetailEvent.SkipInstallmentConfirmed)
                ProgressDialogStatus.Progress -> {
                    // No action required
                }
                ProgressDialogStatus.Success -> dispatchEvent(InstallmentDetailEvent.SkipInstallmentDismiss)
                ProgressDialogStatus.Error -> dispatchEvent(InstallmentDetailEvent.SkipInstallmentDismiss)
            }
        },
        {
            dispatchEvent(InstallmentDetailEvent.SkipInstallmentDismiss)
        }
    )

    val progressDialogVM = ProgressDialogVM(
        {
            dispatchEvent.invoke(InstallmentDetailEvent.DismissLoader)
        },
        {
            dispatchEvent.invoke(InstallmentDetailEvent.DismissLoader)
        }
    )

    fun onTransactionIdCopyClick() {
        dispatchEvent(InstallmentDetailEvent.TransactionIdCopyClick(ResourceManager.getInstance().getString(R.string.rp_transaction_id_copied), utr.get() ?: ""))
    }

    fun onRocketPayTransactionIdCopyClick() {
        dispatchEvent(InstallmentDetailEvent.RocketPayTransactionIdCopyClick(ResourceManager.getInstance().getString(R.string.copied),installmentUtr.get() ?: ""))
    }

    fun onContactUsClick() {
        dispatchEvent(InstallmentDetailEvent.ContactUsClick)
    }

    fun onRetryClick() {
        dispatchEvent(InstallmentDetailEvent.RetryInstallmentClick)
    }

    fun onSkipClick() {
        dispatchEvent(InstallmentDetailEvent.SkipInstallmentClick)
    }

    fun onPenaltyClick(){
        dispatchEvent(InstallmentDetailEvent.ChargePenaltyClick)
    }

    fun onMarkOnPaidClick(){
        dispatchEvent(InstallmentDetailEvent.MarkAsPaidClick)
    }

    fun onEditMarkAsPaidClick(){
        dispatchEvent(InstallmentDetailEvent.MarkAsPaidClick)
    }

    fun handleState(state: InstallmentDetailState) {
        val isManual = state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual
        isManualPayment.set(isManual)
        state.installment?.let { installment ->
            isManuallyCollected.set(installment.isMerchantCollected)
            val installmentStateUi = installment.getInstallmentStatusUi(isManual)
            val backgroundColor = installmentStateUi.background
            stateColor.set(backgroundColor)
            toolbarBackground.set(ResourceManager.getInstance().getDrawable(stateColor.get()))
            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_installment_title, state.installment.serialNumber))

            fromParty.set(state.mandate?.customerDetail?.name)

            if(state.installmentPenalty?.amount != null && state.installmentPenalty.status == InstallmentState.CollectionSuccess){
                penaltyDetailVisibility.set(true)
                penaltyDetailText.set(
                    AmountUtils.format(state.installmentPenalty.amount) + " \u2022 "
                        + DateUtils.getDate(state.installmentPenalty.captureAt ?: 0, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
            }else{
                penaltyDetailVisibility.set(false)
            }

            setManualSummary(state)

            if(state.mandate?.paymentMethodDetail?.method != PaymentMethod.Manual){
                retryVisibility.set(true)
                retryEnable.set(state.installment.retryEnable)
                if (state.installment.retryEnable) {
                    retryTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                    retryCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta))
                } else {
                    retryTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
                    retryCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta_disable))
                }
                retryIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_refresh, retryTextColor.get()))
            }else{
                retryVisibility.set(false)
            }

            if(state.installment.skipEnable != null && state.mandate?.paymentMethodDetail?.method != PaymentMethod.Manual){
                skipVisibility.set(true)
                skipEnable.set(state.installment.skipEnable)
                if (state.installment.skipEnable) {
                    skipTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                    skipCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta))
                } else {
                    skipTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
                    skipCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta_disable))
                }
                skipIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_skip, skipTextColor.get()))
            }else{
                skipVisibility.set(false)
            }

            if(state.installment.chargePenalty != null && state.mandate?.paymentMethodDetail?.method != PaymentMethod.Manual){
                penaltyVisibility.set(true)
                penaltyEnable.set(state.installment.chargePenalty)
                if (state.installment.chargePenalty) {
                    penaltyTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                    penaltyCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta))
                } else {
                    penaltyTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
                    penaltyCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta_disable))
                }
                penaltyIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_penalty_icon, penaltyTextColor.get()))
            }else{
                penaltyVisibility.set(false)
            }

            if(state.installment.markAsPaid != null){
                markAsPaidVisibility.set(true)
                markAsPaidEnable.set(state.installment.markAsPaid)
                if (state.installment.markAsPaid) {
                    markAsPaidTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
                    markAsPaidCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta))
                } else {
                    markAsPaidTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
                    markAsPaidCtaBackground.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_cta_disable))
                }
                markAsPaidIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_tick_within_circle_small, markAsPaidTextColor.get()))
            }else{
                markAsPaidVisibility.set(false)
            }

            installmentUtr.set(installment.installmentUtr.uppercase())

            if (installment.utr.isNullOrEmpty()) {
                utr.set(ResourceManager.getInstance().getString(R.string.rp_utr_missing_msg))
                utrCopyVisibility.set(View.GONE)
            } else {
                utr.set(installment.utr)
                utrCopyVisibility.set(View.VISIBLE)
            }

            if (installment.source == null) {
                fromDetail.set(
                    ResourceManager.getInstance().getString(
                        R.string.rp_source_party,
                        state.mandate?.customerDetail?.name,
                        state.mandate?.customerDetail?.mobileNumber
                    )
                )
            } else {
                if (installment.source.upiId.isNotEmpty()) {
                    fromDetail.set(installment.source.upiId)
                } else if(installment.source.accountHolderName.isNotEmpty()){
                    fromDetail.set(
                        ResourceManager.getInstance().getString(
                            R.string.rp_source_party,
                            installment.source.accountHolderName,
                            installment.source.accountNumber
                        )
                    )
                }else {
                    fromDetail.set(state.mandate?.customerDetail?.name)
                }
            }

            if (installment.destination == null) {
                toDetail.set(
                    ResourceManager.getInstance().getString(
                        R.string.rp_source_party,
                        CommonUseCase.getInstance().getName(),
                        CommonUseCase.getInstance().getMobileNumber()
                    )
                )
            } else {
                toDetail.set(
                    ResourceManager.getInstance().getString(
                        R.string.rp_source_party,
                        installment.destination.accountHolderName,
                        installment.destination.accountNumber
                    )
                )
            }

            settlementButtonVisibility.set(installment.state == InstallmentState.SettlementSuccess && state.paymentOrder != null)
            if(installment.state in arrayOf(InstallmentState.SettlementInitiated, InstallmentState.CollectionSuccess)){
                settlementBannerMessage.set(state.settlementBannerMessage)
                settlementBannerClickable.set(state.settlementBannerMessage.contains("kyc", ignoreCase = true)
                        || state.settlementBannerMessage.contains(SettlementUtils.BANK_ACCOUNT, ignoreCase = true))
            }else{
                settlementBannerMessage.set(null)
                settlementBannerClickable.set(false)
            }

            refundMessageVisibility.set(installment.state == InstallmentState.RefundSuccess)

            amount.set(AmountUtils.formatWithoutRupeeSymbol(installment.amountUI))
            if(state.mandate?.bearer != null &&
                (installment.state == InstallmentState.SettlementSuccess
                        || installment.state == InstallmentState.CollectionSuccess
                        || installment.state == InstallmentState.SettlementInitiated)){
                chargeVisibility.set(true)
                if(installment.charges != null){
                    val totalAmountWithoutCustomerCharges: Double
                    val userAmountWithoutCustomerCharges: Double
                    val chargeAfterDiscount: Double
                    if(installment.charges.merchantChargesAtMandateLevel || installment.charges.customerChargesAtMandateLevel){
                        val customerTotalCharges = installment.charges.charges.find { it.type == ChargeBearer.Customer.value }
                        val installments = state.mandate?.installments ?: 1
                        totalAmountWithoutCustomerCharges = if (customerTotalCharges != null) {
                            installment.charges.perInstallmentAmount - customerTotalCharges.charges / installments + customerTotalCharges.discount / installments
                        } else {
                            installment.charges.perInstallmentAmount
                        }
                        val perMerchantCharges = if(installment.charges.merchantChargesAtMandateLevel){
                            if(installment.serialNumber <= 1) {
                                installment.charges.charges.find { it.type == ChargeBearer.Merchant.value }
                            }else{
                                null
                            }
                        }else{
                            installment.charges.perInstallmentCharges.find { it.type == ChargeBearer.Merchant.value }
                        }
                        chargeAfterDiscount = (perMerchantCharges?.charges ?: 0.0) - (perMerchantCharges?.discount ?: 0.0)
                        userAmountWithoutCustomerCharges = totalAmountWithoutCustomerCharges - chargeAfterDiscount

                    }else{
                        val perCustomerTotalCharges = installment.charges.perInstallmentCharges.find { it.type == ChargeBearer.Customer.value }
                        totalAmountWithoutCustomerCharges = if (perCustomerTotalCharges != null) {
                            installment.charges.perInstallmentAmount - perCustomerTotalCharges.charges + perCustomerTotalCharges.discount
                        } else {
                            installment.charges.perInstallmentAmount
                        }
                        val perMerchantCharges = installment.charges.perInstallmentCharges.find { it.type == ChargeBearer.Merchant.value }
                        chargeAfterDiscount = (perMerchantCharges?.charges ?: 0.0) - (perMerchantCharges?.discount ?: 0.0)
                        userAmountWithoutCustomerCharges = totalAmountWithoutCustomerCharges - chargeAfterDiscount
                    }
                    totalCollectedAmount.set(AmountUtils.format(totalAmountWithoutCustomerCharges))
                    userCollectedAmount.set(AmountUtils.format(userAmountWithoutCustomerCharges))
                    chargeAmount.set(AmountUtils.format(chargeAfterDiscount))
                    chargeLabelText.set(ResourceManager.getInstance().getString(R.string.rp_handling_charges_paid_by_you))
                }else{
                    totalCollectedAmount.set(AmountUtils.format(state.installment.amount))
                    userCollectedAmount.set(AmountUtils.format(state.installment.amountWithoutCharges))
                    chargeAmount.set(AmountUtils.format(state.installment.amount - state.installment.amountWithoutCharges))
                    chargeLabelText.set(if(state.mandate.bearer == ChargeBearer.Customer){
                        ResourceManager.getInstance().getString(R.string.rp_handling_charges_passed_to_customer)
                    }else{
                        ResourceManager.getInstance().getString(R.string.rp_handling_charges_paid_by_you)
                    })
                }

            }else{
                chargeVisibility.set(false)
            }
        }
    }

    private fun setManualSummary(state: InstallmentDetailState){
        manualSummaryVisibility.set(state.installment?.paymentMode != null)
        time.set(DateUtils.getDate(state.installment?.updatedAt ?: 0, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
        mode.set(state.installment?.paymentMode?.translation ?: "--")
        comment.set(state.installment?.comments ?: "--")
    }

    fun onViewSettlementClick(){
        dispatchEvent(InstallmentDetailEvent.ViewSettlementClick)
    }

    fun onSettlementBannerClick(){
        if (settlementBannerMessage.get()?.contains(SettlementUtils.BANK_ACCOUNT) == true) {
            dispatchEvent(InstallmentDetailEvent.AddBankAccountClick)
        }else{
            dispatchEvent(InstallmentDetailEvent.SettlementBannerClick)
        }
    }
}
