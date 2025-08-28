package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.UpiApplication
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet

internal data class MandateAddState(
    val name: String = "",
    val nameError: String? = null,
    val note: String = "",
    val noteError: String? = null,
    val number: String = "",
    val numberError: String? = null,
    var amount: String = "",
    val amountError: String? = null,
    var downPayment: String = "",
    val downPaymentError: String? = null,
    val upiId: String = "",
    val upiIdError: String? = null,
    val installment: Int? = null,
    val installmentError: String? = null,
    val startDate: Long? = null,
    val startDateError: String? = null,
    val installmentFrequency: InstallmentFrequency? = null,
    val installmentFrequencyError: String? = null,
    val product: MandateProduct = MandateProduct.Default,
    val paymentMethod: PaymentMethod? = PaymentMethod.Upi,
    val isGeneratePaymentLinkEnable: Boolean = false,
    val isContactSelected: Boolean = false,
    val installmentFrequencies: List<ItemDialogBottomSheet> = emptyList(),
    val installments: List<ItemDialogBottomSheet> = emptyList(),
    val isUpiEnable: Boolean = true,
    val upiApplication: UpiApplication = UpiApplication.None,
    val mandate: Mandate? = null,
    val charge: Charge? = null,
    val isMandateCreationInitiated: Boolean = false,
    val isMandateExisted: Boolean = false,
    var isPersonalInfoVerified: Boolean = false,
    val isPersonalEditEnabled: Boolean = true,
    var isCollectionInfoVerified: Boolean = false,
    var isPaymentInfoVerified: Boolean = false,
    val isCollectionInInstallmentEnabled: Boolean = false,
    var isCustomerChargeBearer: Boolean = false,
    var coupons: List<Coupon> = emptyList(),
    val selectedCoupon: Coupon? = null,
    val chargeResponse: ChargeResponseDto? = null,
    val referenceId: String? = null,
    val referenceType: String? = null,
    val showSkip: Boolean = false,
    val maxUpiAmountLimit: Int = AmountUtils.NON_MONETISED_UPI_MAXIMUM_AMOUNT,
    val isCashFreeMandateUpiEnabled: Boolean = true,
    val termsAndConditionUrl: String? = null,
    val financier: String? = null,
    val isTokenizationEnabled: Boolean = true,
    val productWallet: ProductWallet? = null
    ) : BaseState(MandateAddScreen)


internal sealed class MandateAddEvent(name: String? = null) : BaseEvent(name) {
    data class Init(val customerName: String, val customerNumber: String, val note: String,
                    val amount: Double, val downPayment: Double, val referenceId: String?,
                    val referenceType: String?, val showSkip: Boolean,
                    val paymentMode: String?, val financier: String?): MandateAddEvent()
    data object LoadProductWallet: MandateAddEvent()
    data class UpdateProductWallet(val productWallet: ProductWallet?): MandateAddEvent()
    data class CustomerNameChanged(val customerName: String) : MandateAddEvent()
    data class CustomerNumberChanged(val customerNumber: String) : MandateAddEvent()
    data class NoteChanged(val note: String) : MandateAddEvent()
    data class AmountChanged(val amount: String) : MandateAddEvent()
    data class DownPaymentChanged(val downPayment: String) : MandateAddEvent()
    object NumberFocusChanged : MandateAddEvent()
    object NameFocusChanged : MandateAddEvent()
    object NoteFocusChanged : MandateAddEvent()
    object AmountFocusChanged : MandateAddEvent()
    object DownPaymentFocusChanged : MandateAddEvent()
    object NachClick : MandateAddEvent()
    object UpiClick : MandateAddEvent()

    data class PaymentLinkGenerated(val mandate: Mandate, val userMobileNumber: String) : MandateAddEvent("")
    data class SharePaymentLinkClick(val mandate: Mandate, val viaSms: Boolean = false) : MandateAddEvent()
    data class UnableToGeneratePaymentLink(val message: String): MandateAddEvent()
    data class SkipSharePaymentLinkClick(val mandate: Mandate) : MandateAddEvent("share_payment_link_skip")

    object InstallmentClick : MandateAddEvent("number_of_installment_click")
    data class InstallmentSelected(val installment: Int) : MandateAddEvent()

    object StartDateClick : MandateAddEvent()
    data class StartDateSelected(val startDate: Long?) : MandateAddEvent()

    object InstallmentFrequencyClick : MandateAddEvent()
    data class InstallmentFrequencySelected(val installmentFrequency: InstallmentFrequency) : MandateAddEvent()

    object ProminentDisclosureAllowed: MandateAddEvent("contact_permission_allowed")
    object ProminentDisclosureDenied: MandateAddEvent("contact_permission_denied")
    object SelectContactClick: MandateAddEvent("select_contact_click")
    data class ContactSelected(val contactName: String?, val contactNumber: String?): MandateAddEvent("contact_selected")

    data class ProgressActionClick(val progressDialogStatus: ProgressDialogStatus): MandateAddEvent()

    object CustomInstallmentClick: MandateAddEvent("custom_installment_click")
    object DismissCustomInstallment: MandateAddEvent()
    object WrongInstallmentSelected: MandateAddEvent()

    data class CustomInstallmentTextChange(val installment: Int): MandateAddEvent()

    object ShowChargeDialog: MandateAddEvent("learn_more_charge_click")

    data class ContinueAnywayClick(val progressDialogStatus: ProgressDialogStatus) : MandateAddEvent()
    data class ChangeDateClick(val progressDialogStatus: ProgressDialogStatus) : MandateAddEvent()
    data class InstallmentFrequencyLoaded(val installmentFrequencies: List<InstallmentFrequency>): MandateAddEvent()

    object CreateMandateNextClick: MandateAddEvent("")
    object EditPersonalInfoClick: MandateAddEvent()
    object EditCollectionInfoClick: MandateAddEvent()
    object CollectInInstallmentEnabled: MandateAddEvent("")
    object BackPressed: MandateAddEvent("")
    data class WhatsAppTemplateCreated(
        val mandate: Mandate,
        val experiment: String,
        val messageTemplate: String,
        val visSms: Boolean
    ) : MandateAddEvent("")
    object ChargeBearerChanged: MandateAddEvent("")
    data class UpdateMoreDetailsState(val flag: Boolean): MandateAddEvent("")
    data class OpenMandatePreview(val mandate: Mandate): MandateAddEvent("show_mandate_preview_dialog")
    data class ComputeCharges(
        val amount: Double, val frequency: String, val installments: Int,
        val chargeBearer: String, val paymentMethod: PaymentMethod): MandateAddEvent()
    data class MandateChargesComputed(val chargeResponse: ChargeResponseDto,
                                      val selectedCoupon: Coupon?, val isCouponSelectedByUser: Boolean) : MandateAddEvent()
    object DismissErrorDialog: MandateAddEvent()
    data class MandateChargesFailed(val error: GenericErrorResponse): MandateAddEvent()
    object RetryComputation: MandateAddEvent()
    object TermsAndConditionClick : MandateAddEvent("terms_and_condition_click")
    object MandateLinkClick: MandateAddEvent("")
    data class RefreshMandate(val mandateId: String): MandateAddEvent("")
    object GoToMandateDetail: MandateAddEvent()
    object StopPolling: MandateAddEvent()
    data object CloseScreen: MandateAddEvent()
}


internal sealed class MandateAddASF : AsyncSideEffect {
    data object LoadProductWallet: MandateAddASF()

    data class CreateMandate(
        val product: String,
        val name: String,
        val number: String,
        val note: String,
        val amount: Double,
        val downPayment: Double,
        val installments: Int,
        val startDate: Long,
        val frequency: String,
        val paymentMethod: PaymentMethod,
        val upiId: String,
        val amountWithoutCharges: Double? = null,
        val bearer: String? = null,
        val chargeId: String? = null,
        val discountId: String? = null,
        val originalAmount: Double,
        val referenceId: String?,
        val referenceType: String?,
        val chargeResponse: ChargeResponseDto? = null,
        val financier: String?
    ) : MandateAddASF()
    object LoadSupportedFrequency: MandateAddASF()
    data class ShareOnWhatsAppClick(val mandate: Mandate, val visSms: Boolean = false): MandateAddASF()
    data class OpenMandatePreview(val mandate: Mandate): MandateAddASF()
    data class CheckChargeAndDiscount(
        val amount: Double, val frequency: String, val installments: Int,
        val chargeBearer: String, val paymentMethod: PaymentMethod): MandateAddASF()
    data class ComputeCharges(val amount: Double, val frequency: String, val installments: Int,
                              val chargeBearer: String, val paymentMethod: PaymentMethod,
                              val coupon: Coupon?, val isCouponSelectedByUser: Boolean = false,
                              val referenceId: String?, val referenceType: String?) : MandateAddASF()
    data class StartPolling(val mandateId: String): MandateAddASF()
    object StopPolling: MandateAddASF()
    data class RefreshMandate(val mandateId: String): MandateAddASF()
}


internal sealed class MandateAddUSF : UiSideEffect {
    object OpenContactSelection: MandateAddUSF()
    object DismissContactSelectionDialog: MandateAddUSF()
    data class ShowProminentDisclosure(val background: Drawable, val title: String, val subtitle: String, val icon: Drawable, val actionText: String, val secondaryActionText: String): MandateAddUSF()
    data class ShowToast(val message: String): MandateAddUSF()
    data class ShowSnackBar(val message: String): MandateAddUSF()
    data class OpenInstallmentSelection(val currentNoOfInstallments: Int?, val installments: List<ItemDialogBottomSheet>) : MandateAddUSF()
    data class OpenStartDateSelection(val installmentFrequency: InstallmentFrequency?, val currentSelectedDate: Long?) : MandateAddUSF()
    data class OpenFrequencySelection(val installmentFrequency: InstallmentFrequency?, val frequencies: List<ItemDialogBottomSheet>) : MandateAddUSF()
    data class OpenMandatePreview(val mandate: Mandate, val referenceId: String?, val showSkip: Boolean, val isManual: Boolean) : MandateAddUSF()
    data class OpenUpiApp(val mandate: Mandate, val upiApplication: UpiApplication) : MandateAddUSF()
    data class GotoMandateDetail(val mandateId: String, val referenceId: String?, val isManual: Boolean): MandateAddUSF()
    data class ShareOnWhatsApp(val mobileNumber: String, val message: String,
                               val mandateId: String, val referenceId: String?,
                               val isManual: Boolean) : MandateAddUSF()
    data class ShareOnSms(val mobileNumber: String, val message: String,
                          val mandateId: String, val referenceId: String?,
                          val isManual: Boolean) : MandateAddUSF()
    data class ShowLoading(val header: String, val message: String) : MandateAddUSF()
    data class ShowError(val header: String, val message: String) : MandateAddUSF()
    object CloseProgressDialog: MandateAddUSF()
    object OpenCustomInstallmentDialog: MandateAddUSF()
    object DismissCustomInstallmentDialog: MandateAddUSF()
    data class UpdateHelperText(val helperText: String): MandateAddUSF()
    data class OpenChargeDialog(val isCashFreeEnabled: Boolean): MandateAddUSF()

    data class ShowDateChangeDialog(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): MandateAddUSF()
    object DismissDateChangeDialog: MandateAddUSF()
    object NumberFocusChanged : MandateAddUSF()
    object NameFocusChanged : MandateAddUSF()
    object NoteFocusChanged : MandateAddUSF()
    object AmountFocusChanged : MandateAddUSF()
    object CloseKeyboard: MandateAddUSF()
    data class ShowErrorDialog(val headerDrawable: Drawable,
                                       val headerBackground: Drawable,
                                       val title: String,
                                       val detail: String,
                                       val actionText: String,
                                       val secondaryBtnText: String?): MandateAddUSF()
    object DismissErrorDialog: MandateAddUSF()
    object CloseScreen: MandateAddUSF()
    object CloseDialog: MandateAddUSF()
    data class OpenLink(val link: String): MandateAddUSF()
    data class ShowWalletError(val title: String, val subtitle: String, val button: String): MandateAddUSF()

}

internal object MandateAddScreen : Screen("mandate_create")
