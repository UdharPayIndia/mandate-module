package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableFloat
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddState
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetenter.BottomSheetEnterVM
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextColor
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.strike
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.main.init.MandateManager

internal class MandateAddUM (private val dispatchEvent: (MandateAddEvent) -> Unit) : BaseMainUM() {

    var isCloseEnabled = false
    var isBackPressHandled = true
    val progressDialogVM = ProgressDialogVM ({
        dispatchEvent(MandateAddEvent.ProgressActionClick(it))
    })

    val contactPermissionDialogVM = ProgressDialogVM (
        { dispatchEvent(MandateAddEvent.ProminentDisclosureAllowed) },
        { dispatchEvent(MandateAddEvent.ProminentDisclosureDenied) }
    )

    val customInstallmentVM = BottomSheetEnterVM(
        title = ResourceManager.getInstance().getString(R.string.rp_custom_installment),
        hint = ResourceManager.getInstance().getString(R.string.rp_customer_installment_hint),
        primaryActionText = ResourceManager.getInstance().getString(R.string.rp_save),
        secondaryActionText = ResourceManager.getInstance().getString(R.string.rp_cancel),
        textChange = {
            dispatchEvent(
                MandateAddEvent.CustomInstallmentTextChange(
                    AmountUtils.stringToInt(
                        it
                    )
                )
            )
        },
        primaryClick = {
            if (it == "1") {
                dispatchEvent(MandateAddEvent.WrongInstallmentSelected)
            } else {
                dispatchEvent(MandateAddEvent.InstallmentSelected(AmountUtils.stringToInt(it)))
                dispatchEvent(MandateAddEvent.DismissCustomInstallment)
            }

        },
        secondaryClick = { dispatchEvent(MandateAddEvent.DismissCustomInstallment) }
    )

    val dateChangeDialogVM = ProgressDialogVM (
        { dispatchEvent(MandateAddEvent.ChangeDateClick(it)) },
        { dispatchEvent(MandateAddEvent.ContinueAnywayClick(it)) }
    )


    val errorDialogVM = ProgressDialogVM (
        { dispatchEvent(MandateAddEvent.RetryComputation) },
        { dispatchEvent(MandateAddEvent.DismissErrorDialog) }
    )
    val walletErrorDialogVM = ProgressDialogVM (
        { dispatchEvent(MandateAddEvent.CloseScreen) },
        { dispatchEvent(MandateAddEvent.CloseScreen) }
    )

    val isPersonalDetailExpanded = ObservableBoolean()
    val isPersonalEditEnabled = ObservableBoolean(true)
    val personalTitleText = ObservableField<String>()
    val personalSubTitleText = ObservableField<String>()

    val name = ObservableField<String>()
    val nameError = ObservableField<String>()

    val number = ObservableField<String>()
    val numberError = ObservableField<String>()

    val note = ObservableField<String>()
    val noteError = ObservableField<String>()

    val amount = ObservableField<String>()
    val amountError = ObservableField<String>()

    val downPayment = ObservableField<String>()
    val downPaymentError = ObservableField<String>()

    val totalBillAmount = ObservableField<String>()

    val collectionDetailVisibility = ObservableBoolean()
    val isCollectionDetailsExpanded = ObservableBoolean()
    val collectionTitleText = ObservableField<String>()
    val collectionSubTitleText = ObservableField<String>()

    val installmentCollectionDrawable = ObservableField<Drawable>()
    val singlePaymentDrawable = ObservableField<Drawable>()

    val installmentVisibility = ObservableInt()
    val installment = ObservableField<String>()
    val installmentError = ObservableField<String>()

    val startDateVisibility = ObservableInt()
    val startDate = ObservableField<String>()
    val startDateError = ObservableField<String>()

    val frequencyVisibility = ObservableInt()
    val installmentFrequency = ObservableField<String>()
    val installmentFrequencyError = ObservableField<String>()

    val selectedCoupon = ObservableField<Coupon>()
    val couponVisibility = ObservableBoolean(false)
    val paymentDetailVisibility = ObservableBoolean()

    val enableUpiLayout = ObservableBoolean()
    val upiLayoutAlpha = ObservableFloat()
    val upiAmountLimitError = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_upi_amount_limit_exceeded,
        AmountUtils.format(AmountUtils.NON_MONETISED_UPI_MAXIMUM_AMOUNT.toDouble())))
    val upiAmountLimitExceededVisibility = ObservableInt(View.GONE)

    val upiLayoutVisibility = ObservableInt()

//    val nashDrawable = ObservableField<Drawable>()
    val upiDrawable = ObservableField<Drawable>()

    val generatePaymentButtonEnabled = ObservableBoolean(true)
    val generatePaymentLinkText = ObservableField<String>()

    val charges = ObservableField<SpannableString>()
    val oldChargeVisibility = ObservableInt(View.GONE)
    val newChargeVisibility = ObservableInt(View.GONE)
    val chargerControlVisibility = ObservableBoolean(false)

    val subscriptionInfoText = ObservableField<String>()
    val collectionAmountText = ObservableField<String>()
    val handlingChargeLabel = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_merchant_charges))
    val handlingChargeText = ObservableField<SpannableString>()
    val userCollectionPerInstallmentLabel = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_your_collection))
    val userCollectionPerInstallmentText = ObservableField<String>()
    val bearerCheckBox = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_empty))

    fun onChargeClick() {
        dispatchEvent(MandateAddEvent.ShowChargeDialog)
    }

    fun onCustomerNameChanged(customerName: CharSequence) {
        dispatchEvent(MandateAddEvent.CustomerNameChanged(customerName.toString()))
    }

    fun onCustomerNumberChanged(customerNumber: CharSequence) {
        dispatchEvent(MandateAddEvent.CustomerNumberChanged(customerNumber.toString()))
    }

    fun onOrderDescriptionChanged(orderDescription: CharSequence) {
        dispatchEvent(MandateAddEvent.NoteChanged(orderDescription.toString()))
    }

    fun onAmountChanged(amount: CharSequence) {
        dispatchEvent(MandateAddEvent.AmountChanged(amount.toString()))
    }

    fun onDownPaymentChanged(amount: CharSequence) {
        dispatchEvent(MandateAddEvent.DownPaymentChanged(amount.toString()))
    }


    fun onNumberFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateAddEvent.NumberFocusChanged)
        }
    }

    fun onNameFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateAddEvent.NameFocusChanged)
        }
    }

    fun onNoteFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateAddEvent.NoteFocusChanged)
        }
    }

    fun onAmountFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateAddEvent.AmountFocusChanged)
        }
    }

    fun onDownPaymentFocusChanged(hasFocus: Boolean) {
        if (hasFocus) {
            dispatchEvent(MandateAddEvent.DownPaymentFocusChanged)
        }
    }

//    fun onNashClick() {
//        dispatchEvent(MandateAddEvent.NachClick)
//    }

    fun onUpiClick() {
        dispatchEvent(MandateAddEvent.UpiClick)
    }

    fun handleState(state: MandateAddState) {
        if(state.paymentMethod == PaymentMethod.Manual){
            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_create_manual_emi))
        }else{
            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_create_new_payment))
        }
        isBackPressHandled = state.isPersonalInfoVerified && state.isPersonalEditEnabled

        isPersonalEditEnabled.set(state.isPersonalEditEnabled)
        setCardVisibility(state)
        setCollectionDetails(state)

        name.set(state.name)
        nameError.set(state.nameError)

        number.set(state.number)
        numberError.set(state.numberError)

        note.set(state.note)
        noteError.set(state.noteError)

        amount.set(state.amount)
        amountError.set(state.amountError)

        downPayment.set(state.downPayment)
        downPaymentError.set(state.downPaymentError)

        totalBillAmount.set(AmountUtils.format(AmountUtils.stringToDouble(state.amount) + AmountUtils.stringToDouble(state.downPayment)))

        if (state.installment == null) {
            installment.set(ResourceManager.getInstance().getString(R.string.rp_no_of_installments))
        } else {
            installment.set(ResourceManager.getInstance().getString(R.string.rp_x_no_of_installments, state.installment))
        }

        if (state.startDate == null) {
            if(state.installmentFrequency is InstallmentFrequency.OneTimePayment){
                startDate.set(ResourceManager.getInstance().getString(R.string.rp_collection_date))
            }else{
                startDate.set(ResourceManager.getInstance().getString(R.string.rp_start_date))
            }
        } else {
            startDate.set(DateUtils.getDate(state.startDate, DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR))
        }

        if (state.installmentFrequency == null) {
            installmentFrequency.set(ResourceManager.getInstance().getString(R.string.rp_installment_frequency))
        } else {
            installmentFrequency.set(ResourceManager.getInstance().getString(state.installmentFrequency.suffix_ly))
        }

        startDateError.set(state.startDateError)
        installmentFrequencyError.set(state.installmentFrequencyError)
        installmentError.set(state.installmentError)

        setPaymentDetails(state)
        showMonetization(state)

    }

    private fun setPaymentDetails(state: MandateAddState) {
        if(state.paymentMethod != PaymentMethod.Manual){
            upiAmountLimitError.set(
                ResourceManager.getInstance().getString(
                    R.string.rp_upi_amount_limit_exceeded,
                    AmountUtils.format(state.maxUpiAmountLimit.toDouble())
                )
            )
            if (state.isUpiEnable) {
                enableUpiLayout.set(true)
                upiLayoutAlpha.set(1.0F)
                upiAmountLimitExceededVisibility.set(View.GONE)
                upiLayoutVisibility.set(View.VISIBLE)
            } else {
                enableUpiLayout.set(false)
                upiLayoutAlpha.set(0.3F)

                if (AmountUtils.stringToDouble(state.amount).div(state.installment ?: 1) > 5000) {
                    upiAmountLimitExceededVisibility.set(View.VISIBLE)
                } else {
                    upiAmountLimitExceededVisibility.set(View.GONE)
                }

                if (state.installmentFrequency is InstallmentFrequency.Adhoc) {
                    upiLayoutVisibility.set(View.GONE)
                } else {
                    upiLayoutVisibility.set(View.VISIBLE)
                }
            }

            when (state.paymentMethod) {
//                PaymentMethod.Nach -> {
//                    nashDrawable.set(
//                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled)
//                    )
//                    upiDrawable.set(
//                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty)
//                    )
//                }

                PaymentMethod.Upi -> {
//                    nashDrawable.set(
//                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty)
//                    )
                    upiDrawable.set(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled)
                    )
                }

                else -> {
                    upiDrawable.set(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty)
                    )
                }
            }
        }
    }

    private fun showMonetization(state: MandateAddState) {
        if (state.installmentFrequency != null
            && state.installmentFrequency !is InstallmentFrequency.Adhoc
            && state.paymentMethod != PaymentMethod.Manual) {
            oldChargeVisibility.set(View.GONE)
            selectedCoupon.set(state.selectedCoupon)
            if(state.isCollectionInfoVerified){
                couponVisibility.set(state.coupons.isNotEmpty())
                newChargeVisibility.set(View.VISIBLE)
                setChargeBearer(state)
                setNewChargesInfo(state)
            } else {
                couponVisibility.set(false)
                newChargeVisibility.set(View.GONE)
                subscriptionInfoText.set(null)
            }
        } else {
            couponVisibility.set(false)
            oldChargeVisibility.set(View.GONE)
            newChargeVisibility.set(View.GONE)
        }
    }

    private fun setChargeBearer(state: MandateAddState){
        if(state.chargeResponse?.isBearerControlAvailable == true){
            chargerControlVisibility.set(true)
            if(state.isCustomerChargeBearer){
                bearerCheckBox.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_filled))
            }else{
                bearerCheckBox.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_checkbox_empty))
            }
        }else{
            chargerControlVisibility.set(false)
        }
    }

    private fun setNewChargesInfo(state: MandateAddState){
        if(state.chargeResponse != null){
            setTotalCollectionAmount(state.chargeResponse, state.installment ?: 1)
            val charges = setMerchantCharges(state.chargeResponse, state.installment ?: 1)
            setUserCollectionAmount(state.chargeResponse, state.installment ?: 1)
            setSubscriptionCharges(state, state.installment ?: 1, charges)
        }else{
            handlingChargeLabel.set(ResourceManager.getInstance().getString(R.string.rp_merchant_charges))
            collectionAmountText.set(null)
            handlingChargeText.set(null)
            userCollectionPerInstallmentText.set(null)
            subscriptionInfoText.set(null)
        }
    }

    private fun setSubscriptionCharges(state: MandateAddState, installment: Int, charges: Double){
        val merchantCharges = state.chargeResponse?.charges?.find { it.type == ChargeBearer.Merchant.value }
        val freeByTokenization = state.isTokenizationEnabled && charges == 0.0
        if(merchantCharges != null && (freeByTokenization)){
            if(state.chargeResponse?.showAtMandateLevel == true || installment <= 1){
                handlingChargeLabel.set(ResourceManager.getInstance().getString(
                    R.string.rp_merchant_charges_at_zero, MandateManager.getInstance().getAppName()))
            }else{
                handlingChargeLabel.set(ResourceManager.getInstance().getString(
                    R.string.rp_merchant_charges_per_installment_at_zero, MandateManager.getInstance().getAppName()))
            }
            subscriptionInfoText.set(ResourceManager.getInstance().getString(R.string.rp_saved_with_app_plan,
                AmountUtils.format(merchantCharges.discount), MandateManager.getInstance().getAppName()))
        }else{
            if(state.chargeResponse?.showAtMandateLevel == true || installment <= 1){
                handlingChargeLabel.set(ResourceManager.getInstance().getString(R.string.rp_merchant_charges))
            }else{
                handlingChargeLabel.set(ResourceManager.getInstance().getString(R.string.rp_merchant_charges_per_installment))
            }
            subscriptionInfoText.set(null)
        }
    }

    private fun setTotalCollectionAmount(chargeResponse: ChargeResponseDto, installment: Int) {
        if (chargeResponse?.showAtMandateLevel == true || installment <= 1) {
            val customerTotalCharges = chargeResponse.charges.find { it.type == ChargeBearer.Customer.value }
            val amountWithoutCustomerCharges = if (customerTotalCharges != null) {
                chargeResponse.amount - customerTotalCharges.charges + customerTotalCharges.discount
            } else {
                chargeResponse.amount
            }
            collectionAmountText.set(AmountUtils.format(amountWithoutCustomerCharges))
        }else{
            val perCustomerTotalCharges = chargeResponse.perInstallmentCharges.find { it.type == ChargeBearer.Customer.value }
            val amountWithoutCustomerCharges = if (perCustomerTotalCharges != null) {
                chargeResponse.perInstallmentAmount - perCustomerTotalCharges.charges + perCustomerTotalCharges.discount
            } else {
                chargeResponse.perInstallmentAmount
            }
            collectionAmountText.set(ResourceManager.getInstance().getString(R.string.rp_installment_of_amount_each,
                installment, AmountUtils.format(amountWithoutCustomerCharges)))
        }
    }

    private fun setMerchantCharges(chargeResponse: ChargeResponseDto, installment: Int): Double {
        if(chargeResponse?.showAtMandateLevel == true || installment <= 1){
            val merchantCharges = chargeResponse.charges.find { it.type == ChargeBearer.Merchant.value }
            if(merchantCharges != null){
                val chargeBeforeDiscount = AmountUtils.format(merchantCharges.charges)
                if(merchantCharges.discount > 0){
                    val chargeAfterDiscount = AmountUtils.format(merchantCharges.charges - merchantCharges.discount)
                    handlingChargeText.set(ResourceManager.getInstance().getString(R.string.rp_charge_with_discount,
                        chargeBeforeDiscount,
                        chargeAfterDiscount)
                        .getSpannable()
                        .setTextColor(chargeBeforeDiscount, ResourceManager.getInstance().getColor(R.color.rp_green_3))
                        .strike(chargeBeforeDiscount))
                    return merchantCharges.charges - merchantCharges.discount
                }else{
                    handlingChargeText.set(SpannableString(chargeBeforeDiscount))
                    return merchantCharges.charges
                }
            }else{
                handlingChargeText.set(SpannableString(AmountUtils.format(0.0)))
                return 0.0
            }
        }else{
            val perMerchantCharges = chargeResponse.perInstallmentCharges.find { it.type == ChargeBearer.Merchant.value }
            if(perMerchantCharges != null){
                val chargeBeforeDiscount = AmountUtils.format(perMerchantCharges.charges)
                if(perMerchantCharges.discount > 0){
                    val chargeAfterDiscount = AmountUtils.format(perMerchantCharges.charges - perMerchantCharges.discount)
                    handlingChargeText.set(ResourceManager.getInstance().getString(R.string.rp_charge_with_discount,
                        chargeBeforeDiscount,
                        chargeAfterDiscount)
                        .getSpannable()
                        .setTextColor(chargeBeforeDiscount, ResourceManager.getInstance().getColor(R.color.rp_green_3))
                        .strike(chargeBeforeDiscount))
                    return perMerchantCharges.charges - perMerchantCharges.discount
                }else{
                    handlingChargeText.set(SpannableString(chargeBeforeDiscount))
                    return perMerchantCharges.charges
                }
            }else{
                handlingChargeText.set(SpannableString(AmountUtils.format(0.0)))
                return 0.0
            }
        }

    }

    private fun setUserCollectionAmount(chargeResponse: ChargeResponseDto, installment: Int){
        if(chargeResponse?.showAtMandateLevel == true || installment <= 1){
            val amountWithoutCharges = chargeResponse.amountWithoutCharges
            if(amountWithoutCharges < 0.0){
                userCollectionPerInstallmentText.set("-" + AmountUtils.format(amountWithoutCharges))
            }else{
                userCollectionPerInstallmentText.set(AmountUtils.format(amountWithoutCharges))
            }
            userCollectionPerInstallmentLabel.set(ResourceManager.getInstance().getString(R.string.rp_your_collection))
        }else{
            val perAmountWithoutCharges = chargeResponse.perInstallmentAmountWithoutCharges
            if(perAmountWithoutCharges < 0.0){
                userCollectionPerInstallmentText.set("-" + AmountUtils.format(perAmountWithoutCharges))
            }else{
                userCollectionPerInstallmentText.set(AmountUtils.format(perAmountWithoutCharges))
            }
            userCollectionPerInstallmentLabel.set(ResourceManager.getInstance().getString(R.string.rp_your_collection_per_installment))
        }
    }

    private fun setCardVisibility(state: MandateAddState) {
        when{
            !state.isPersonalInfoVerified -> {
                isPersonalDetailExpanded.set(true)
                collectionDetailVisibility.set(false)
                paymentDetailVisibility.set(false)
                generatePaymentLinkText.set(ResourceManager.getInstance().getString(R.string.rp_next))
                generatePaymentButtonEnabled.set(true)
            }
            !state.isCollectionInfoVerified -> {
                isPersonalDetailExpanded.set(false)
                collectionDetailVisibility.set(true)
                isCollectionDetailsExpanded.set(true)
                paymentDetailVisibility.set(false)
                setPersonalInfo(state)

                if(state.paymentMethod == PaymentMethod.Manual){
                    generatePaymentLinkText.set(ResourceManager.getInstance().getString(R.string.rp_create_payment_schedule))
                }else{
                    generatePaymentLinkText.set(ResourceManager.getInstance().getString(R.string.rp_next))
                }
                generatePaymentButtonEnabled.set(true)

            }
            else -> {
                isPersonalDetailExpanded.set(false)
                setPersonalInfo(state)
                collectionDetailVisibility.set(true)
                if(state.paymentMethod == PaymentMethod.Manual){
                    isCollectionDetailsExpanded.set(true)
                    paymentDetailVisibility.set(false)
                    generatePaymentLinkText.set(ResourceManager.getInstance().getString(R.string.rp_create_payment_schedule))
                    generatePaymentButtonEnabled.set(true)
                }else{
                    isCollectionDetailsExpanded.set(false)
                    setCollectionInfo(state)
                    paymentDetailVisibility.set(true)
                    generatePaymentLinkText.set(ResourceManager.getInstance().getString(R.string.rp_share_payment_link))
                    generatePaymentButtonEnabled.set(
                        if(state.installmentFrequency !is InstallmentFrequency.Adhoc){
                            state.chargeResponse != null
                        }else{
                            true
                        })
                }

            }
        }
    }

    private fun setCollectionDetails(state: MandateAddState){
        when{
            state.isCollectionInInstallmentEnabled -> {
                installmentCollectionDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
                singlePaymentDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                if(state.installmentFrequency is InstallmentFrequency.Adhoc) {
                    installmentVisibility.set(View.GONE)
                    startDateVisibility.set(View.GONE)
                }else{
                    installmentVisibility.set(View.VISIBLE)
                    startDateVisibility.set(View.VISIBLE)
                }
                frequencyVisibility.set(View.VISIBLE)
            }
            state.installmentFrequency == InstallmentFrequency.OneTimePayment -> {
                singlePaymentDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
                installmentCollectionDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                installmentVisibility.set(View.GONE)
                frequencyVisibility.set(View.GONE)
                startDateVisibility.set(View.VISIBLE)
            }
            else -> {
                installmentCollectionDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                singlePaymentDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                installmentVisibility.set(View.GONE)
                frequencyVisibility.set(View.GONE)
                startDateVisibility.set(View.GONE)
            }
        }
    }

    private fun setPersonalInfo(state: MandateAddState){
        personalTitleText.set("${AmountUtils.format(AmountUtils.stringToDouble(state.amount))} \u2022 ${state.name}")
        personalSubTitleText.set(state.note)
    }

    private fun setCollectionInfo(state: MandateAddState){
        val totalAmount = AmountUtils.format(AmountUtils.stringToDouble(state.amount))
        val totalNumberOfInstallment =  state.installment ?: 1
        val perInstallmentAmount = AmountUtils.format(AmountUtils.stringToDouble(state.amount) / totalNumberOfInstallment)
        val installmentPerText = ResourceManager.getInstance().getString(state.installmentFrequency?.suffix_s ?: InstallmentFrequency.Adhoc.suffix_s)

        collectionTitleText.set(ResourceManager.getInstance().getString(R.string.rp_payment_installment_info,
            perInstallmentAmount, totalNumberOfInstallment, installmentPerText, totalAmount))
        collectionSubTitleText.set("${ResourceManager.getInstance().getString(R.string.rp_collection_date)} ${DateUtils.getDate(state.startDate ?: 0L,
            DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR)}")
    }

    fun onEditPersonalDetails(){
        dispatchEvent(MandateAddEvent.EditPersonalInfoClick)
    }

    fun onEditCollectionDetails(){
        dispatchEvent(MandateAddEvent.EditCollectionInfoClick)
    }

    fun onCollectInInstallmentClick() {
        dispatchEvent(MandateAddEvent.CollectInInstallmentEnabled)
    }

    fun onSinglePaymentClick() {
        dispatchEvent(MandateAddEvent.InstallmentFrequencySelected(InstallmentFrequency.OneTimePayment))
    }

    fun onInstallmentClick() {
        dispatchEvent(MandateAddEvent.InstallmentClick)
    }

    fun onStartDateClick() {
        dispatchEvent(MandateAddEvent.StartDateClick)
    }

    fun onInstallmentFrequencyClick() {
        dispatchEvent(MandateAddEvent.InstallmentFrequencyClick)
    }

    fun onGeneratePaymentLinkClick() {
        dispatchEvent(MandateAddEvent.CreateMandateNextClick)
    }

    fun onBearerCheckBoxClicked(){
        dispatchEvent(MandateAddEvent.ChargeBearerChanged)
    }

}
