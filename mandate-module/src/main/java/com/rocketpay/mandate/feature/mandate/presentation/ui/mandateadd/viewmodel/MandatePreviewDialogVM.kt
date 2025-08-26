package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import android.graphics.Bitmap
import android.text.SpannableString
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.underline
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.QrGeneratorUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class MandatePreviewDialogVM(val mandate: Mandate, val showSkip: Boolean, private val dispatchEvent: (MandateAddEvent) -> Unit) : BaseMainUM() {

    val contactDetails = ObservableField<String>()
    val amount = ObservableField<String>()

    val moreDetailVisibility = ObservableBoolean(false)

    val tennerVisibility = ObservableBoolean()
    val tennerTime = ObservableField<String>()

    val installmentAmountLabel = ObservableField<String>()
    val installmentAmount = ObservableField<String>()

    val startDateLabel = ObservableField<String>()
    val startDate = ObservableField<String>()

    val customerChargesVisibility = ObservableBoolean()
    val customerChargesText = ObservableField<String>()

    val linkText = ObservableField<SpannableString>()
    val qrBitmap = ObservableField<Bitmap>()
    val qrVisibility = ObservableBoolean()

    val paymentDetailStateText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_view_payment_details))
    val secondaryButtonText = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_share_request_via_sms))
    val termsAndConditions = ObservableField<SpannableString>()

    fun onTermsAndConditionClick() {
        updatePaymentDetailsState(false)
        dispatchEvent(MandateAddEvent.TermsAndConditionClick)
    }

    fun onMandateLinkClick(){
        updatePaymentDetailsState(false)
        dispatchEvent(MandateAddEvent.MandateLinkClick)
    }

    fun onSharePaymentViaWhatsappClick() {
        updatePaymentDetailsState(false)
        dispatchEvent(MandateAddEvent.SharePaymentLinkClick(mandate))
    }

    fun onSharePaymentViaSmsClick() {
        updatePaymentDetailsState(false)
        dispatchEvent(MandateAddEvent.SharePaymentLinkClick(mandate, viaSms = true))
    }


    fun onSecondaryButtonClick() {
        updatePaymentDetailsState(false)
        if (showSkip) {
            dispatchEvent(MandateAddEvent.SkipSharePaymentLinkClick(mandate))
        }
    }

    fun onCardClick(){
        updatePaymentDetailsState(false)
    }

    fun onMoreClick(){
        updatePaymentDetailsState(!moreDetailVisibility.get())
    }

    private fun updatePaymentDetailsState(newState: Boolean){
        if(newState){
            paymentDetailStateText.set(ResourceManager.getInstance().getString(R.string.rp_hide_payment_details))
        }else{
            paymentDetailStateText.set(ResourceManager.getInstance().getString(R.string.rp_view_payment_details))
        }
        moreDetailVisibility.set(newState)
        dispatchEvent(MandateAddEvent.UpdateMoreDetailsState(newState))
    }

    init {
        amount.set(AmountUtils.format(mandate.getMandateAmount()))
        contactDetails.set(mandate.customerDetail.name +" \u2022 " + mandate.customerDetail.mobileNumber)
        linkText.set(mandate.mandateUrl.getSpannable().underline(mandate.mandateUrl))

        val termsAndCondition = ResourceManager.getInstance().getString(R.string.rp_terms_n_condition)
        val termsAndConditionSubtitle = ResourceManager.getInstance().getString(
            R.string.rp_by_accepting_this_mandate_i_consent_to_term,
            BuildConfig.APP_NAME,
            termsAndCondition)
            .getSpannable()
            .setTextColor(termsAndCondition, ResourceManager.getInstance().getColor(R.color.rp_blue_2))
            .underline(termsAndCondition)
        termsAndConditions.set(termsAndConditionSubtitle)

        val qrData = mandate?.meta?.selfCheckoutDto?.qr?.authData?.qrData
        if(!qrData.isNullOrEmpty()){
            qrVisibility.set(true)
            val bitmap = QrGeneratorUtils.generateQrCodeFromUrl(qrData.toString())
            qrBitmap.set(bitmap)
        }else{
            qrVisibility.set(false)
        }

        when(mandate.frequency){
            InstallmentFrequency.OneTimePayment -> {
                tennerVisibility.set(false)
                installmentAmountLabel.set(ResourceManager.getInstance().getString(R.string.rp_collect_one_time))
                startDateLabel.set(ResourceManager.getInstance().getString(R.string.rp_collect_on))
            }
            else -> {
                tennerVisibility.set(true)
                installmentAmountLabel.set(ResourceManager.getInstance().getString(R.string.rp_amount_per_installments))
                startDateLabel.set(ResourceManager.getInstance().getString(R.string.rp_starting_from))
            }
        }
        tennerTime.set("${mandate.installments} (${ResourceManager.getInstance().getString(mandate.frequency.suffix_ly)})")
        val formatInstallAmount = AmountUtils.format(mandate.getMandateInstallmentAmount())
        installmentAmount.set(formatInstallAmount)
        val formatStartDateWithTwoDigitYear = DateUtils.getDate(mandate.nextChargeAt, DateUtils.SLASH_DATE_FORMAT_WITH_FOUR_DIGIT_YEAR)
        startDate.set(formatStartDateWithTwoDigitYear)

        val customerCharges = mandate.meta?.charges?.charges?.find { it.type == ChargeBearer.Customer.value }
        if(customerCharges != null){
            customerChargesVisibility.set(qrData != null)
            customerChargesText.set(AmountUtils.format(customerCharges.charges - customerCharges.discount))
        }else{
            customerChargesVisibility.set(false)
        }

        secondaryButtonText.set(if(showSkip){
            ResourceManager.getInstance().getString(R.string.rp_skip_i_will_do_it_later)
        }else{
            null
        })
    }
}
