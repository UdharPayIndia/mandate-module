package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine

import android.graphics.drawable.Drawable
import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class KycState(
    val type: String = "",
    val kyc: Kyc? = null,
    val currentKycItem: KycWorkFlow? = null,
    val bankAccounts: List<BankAccount> = emptyList(),
    val isSkipJourneyEnabled: Boolean = false,
    val errorCode: String = "",
    val nonBusinessEnumString: String = MandateManager.getInstance().nonBusinessEnumString
) : BaseState(KycScreen)


internal sealed class KycEvent(name: String? = null) : BaseEvent(name) {
    data object LoadKyc : KycEvent()
    data class KycLoaded(val kyc: Kyc?, val kycType: String) : KycEvent("")
    data object RefreshKyc: KycEvent()
    data class SubmitItem(val kycItem: KycWorkFlow, val jsonObject: JsonObject): KycEvent("")
    data class SubmissionSuccess(val kycItem: KycWorkFlow): KycEvent("")
    data class SubmissionFailed(val kycItem: KycWorkFlow, val code: String, val message: String): KycEvent("")
    data class ShowIdentityIntroBottomSheet(val kycItem: KycWorkFlow): KycEvent("owner_identity_kyc_submit_click")
    data class InitItem(val kycItem: KycWorkFlow): KycEvent("")
    data class InitSuccess(val kycItem: KycWorkFlow?) : KycEvent("")
    data class InitFailed(val kycItem: KycWorkFlow, val code: String, val message: String): KycEvent("")
    object ProgressDialogPrimaryButtonClick: KycEvent("kyc_error_bottomsheet_retry_click")
    object ProgressDialogSecondaryButtonClick: KycEvent("kyc_error_bottomsheet_skip_click")
    object OwnerIdentityDetailSubmitted: KycEvent("")
    object ContactUsClick: KycEvent("kyc_contact_us_click")
    data class SdkError(val errorCode: Int?, val message: String): KycEvent("kyc_hyper_verge_error")
    object ShowBackPressConfirmationDialog: KycEvent()
    object BackPressConfirmed: KycEvent("owner_kyc_exit_popup_exit_click")
    object BackPressSkipped: KycEvent("owner_kyc_exit_popup_continue_kyc_form_click")

    data class UpdateSkipFlag(val isSkipEnabled: Boolean): KycEvent()
    data class ShowKycSateDialog(
        val drawable: Drawable,
        val icon: Drawable,
        val title: String,
        val subTitle: String,
        val primaryButton: String,
        val secondaryButton: String?
    ) :KycEvent()

    data class MultiChoiceInputClick(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val title: String,
        val subTitle: String?,
        val options: List<String>,
        val jsonObject: JsonObject): KycEvent()
    data class MultiChoiceOptionSelected(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val value: String,
        val jsonObject: JsonObject
    ): KycEvent()
    data class UploadDocumentClick(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val title: String,
        val subTitle: String,
        val allowedExtension: List<String>,
        val sizeLimit: Int?,
        val jsonObject: JsonObject): KycEvent()
    data class DocumentSelected(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val url: String,
        val jsonObject: JsonObject,
        val fileSize: Double
    ): KycEvent()
    data class UploadDocumentSuccessful(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val url: String,
        val jsonObject: JsonObject
    ): KycEvent()
    data class UploadDocumentFailed(
        val message: String
    ) : KycEvent()
}


internal sealed class KycASF : AsyncSideEffect {
    data object Load : KycASF()
    data object Refresh: KycASF()
    data class SubmitItem(val kycItem: KycWorkFlow, val jsonObject: JsonObject, val delay: Long = 0L): KycASF()
    data class InitItem(val kycItem: KycWorkFlow): KycASF()
    data class UploadDocument(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val uri: String,
        val jsonObject: JsonObject,
        val fileSize: Double): KycASF()
}


internal sealed class KycUSF : UiSideEffect {
    data class ShowProgress(val header: String, val message: String) : KycUSF()
    data class ShowFailed(val header: String, val message: String, val primaryButtonText: String, val secondaryButtonText: String?): KycUSF()
    object DismissProgressDialog: KycUSF()
    data class UpdateKyc(val kyc: Kyc, val currentKycItem: KycWorkFlow?, val ownerName: String?, val kycType: String?): KycUSF()
    data class GotoHyperVerge(val type: String?, val kycItemInitMetaHyperVerge: JsonObject?) : KycUSF()
    object ContactUs: KycUSF()
    object ShowBackPressConfirmationDialog: KycUSF()
    object CloseKyc: KycUSF()
    data class ShowIdentityIntroBottomSheet(val name: String?): KycUSF()
    data class ShowKycStateDialog(val drawable: Drawable,
                                  val icon: Drawable,
                                  val title: String,
                                  val subTitle: String,
                                  val primaryButton: String,
                                  val secondaryButton: String?): KycUSF()
    data class OpenMultiChoiceOptionSelection(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val title: String,
        val subTitle: String?,
        val options: List<ItemDialogBottomSheet>,
        val jsonObject: JsonObject): KycUSF()
    data class TriggerNextInputAction(
        val workFlow: KycWorkFlow,
        val nextInput: KycItemInputMeta,
        val jsonObject: JsonObject
    ) : KycUSF()
    data class ShowUploadDocumentDialog(
        val workFlow: KycWorkFlow,
        val inputName: KycItemInputMeta,
        val title: String,
        val subTitle: String?,
        val allowedExtension: List<String>,
        val sizeLimit: Int?,
        val jsonObject: JsonObject): KycUSF()
    data class ShowNonBusinessConfirmationDialog(
        val drawable: Drawable,
        val icon: Drawable,
        val title: String,
        val subTitle: String,
        val primaryButton: String,
        val secondaryButton: String?,
        val kycWorkFlow: KycWorkFlow,
        val jsonObject: JsonObject
    ): KycUSF()
}

internal object KycScreen : Screen("kyc")
