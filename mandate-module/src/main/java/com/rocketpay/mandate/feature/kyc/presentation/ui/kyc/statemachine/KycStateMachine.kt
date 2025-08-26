package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine

import android.net.Uri
import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.image.domain.usecase.ImageUseCase
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDataStore
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycInputTypeEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemState
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.ItemDialogBottomSheet
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.int
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.kyc.domain.entities.KycErrorCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal class KycStateMachine(
    private val kycUseCase: KycUseCase,
    private val imageUseCase: ImageUseCase,
    private val propertyUseCase: PropertyUseCase
) : SimpleStateMachineImpl<KycEvent, KycState, KycASF, KycUSF>(
    KycAnalyticsHandler()
) {

    override fun startState(): KycState {
        return KycState()
    }

    override fun handleEvent(
        event: KycEvent,
        state: KycState
    ): Next<KycState?, KycASF?, KycUSF?> {
        return when (event) {
            is KycEvent.LoadKyc -> {
                next(KycASF.Load)
            }
            is KycEvent.KycLoaded -> {
                if (event.kyc == null) {
                    noChange()
                } else {
                    val currentKycItem = getCurrentKycItem(event.kyc)
                    event.kyc.workflow.forEachIndexed { index, item ->
                        if(index !=0){
                            val previousStep = event.kyc.workflow[index - 1]
                            item.isPreviousStepPending = (previousStep.status !is KycItemState.Completed || previousStep.isPreviousStepPending)
                        }
                    }
                    val ownerName = event.kyc?.workflow?.find { it.name == KycWorkFlowName.BusinessProof }?.output?.get(
                        BUSINESS_OWNER_NAME_KEY)?.asString
                    next(
                        state.copy(kyc = event.kyc, type = event.kycType, currentKycItem = currentKycItem),
                        KycUSF.UpdateKyc(event.kyc, currentKycItem, ownerName, event.kycType)
                    )
                }
            }
            is KycEvent.RefreshKyc -> {
                next(KycASF.Refresh)
            }
            is KycEvent.SubmitItem -> {
                next(
                    KycASF.SubmitItem(event.kycItem, event.jsonObject),
                    KycUSF.ShowProgress(getProgressTitle(event.kycItem), getProgressSubTitle(event.kycItem))
                )
            }
            is KycEvent.SubmissionSuccess -> {
                next(state.copy(errorCode = ""), KycUSF.DismissProgressDialog)
            }
            is KycEvent.SubmissionFailed -> {
                if (state.currentKycItem == null) {
                    noChange()
                } else {
                    val kycErrorCode = KycErrorCode.get(event.code)
                    val title = ResourceManager.getInstance().getString(kycErrorCode.title)
                    val message = event.message
                    next(state.copy(errorCode = event.code), KycUSF.ShowFailed(title, message,
                        ResourceManager.getInstance().getString(R.string.rp_retry),
                        if(state.isSkipJourneyEnabled){
                            ResourceManager.getInstance().getString(R.string.rp_skip)
                        }else{
                            null
                        }))
                }
            }
            is KycEvent.ShowIdentityIntroBottomSheet -> {
                val panKycItem = state.kyc?.workflow?.find { it.name == KycWorkFlowName.PanDetailsVerification }
                val panName = panKycItem?.output?.get(PAN_NAME_KEY)?.asString?.toCapitalise() ?: ""
                next(KycUSF.ShowIdentityIntroBottomSheet(panName))
            }
            is KycEvent.InitItem -> {
                next(
                    KycASF.InitItem(event.kycItem),
                    KycUSF.ShowProgress(
                        ResourceManager.getInstance().getString(R.string.rp_owner_details_init_in_progress_title),
                        ResourceManager.getInstance().getString(R.string.rp_owner_details_init_in_progress_subtitle)
                    )
                )
            }
            is KycEvent.InitSuccess -> {
                next(state.copy(errorCode = ""),
                    KycUSF.GotoHyperVerge(
                        event.kycItem?.type,
                        event.kycItem?.output)
                )
            }
            is KycEvent.InitFailed -> {
                next(
                    state.copy(errorCode = KycErrorCode.KycInitError.value),
                    KycUSF.ShowFailed(
                        ResourceManager.getInstance().getString(R.string.rp_owner_details_init_failed_title),
                        event.message,
                        ResourceManager.getInstance().getString(R.string.rp_dismiss),
                        if(state.isSkipJourneyEnabled){
                            ResourceManager.getInstance().getString(R.string.rp_skip)
                        }else{
                            null
                        }
                    )
                )
            }
            is KycEvent.ProgressDialogPrimaryButtonClick, KycEvent.BackPressSkipped -> {
                next(KycUSF.DismissProgressDialog)
            }
            is KycEvent.OwnerIdentityDetailSubmitted -> {
                val kycItems = state.kyc?.workflow?.filter { it.name == KycWorkFlowName.SdkWorkFlow }
                if (kycItems.isNullOrEmpty()) {
                    noChange()
                } else {
                    val kycItem = kycItems.first()
                    next(
                        state.copy(errorCode = ""),
                        KycASF.SubmitItem(kycItem, JsonObject(), 3000L),
                        KycUSF.ShowProgress(getProgressTitle(kycItem), getProgressSubTitle(kycItem))
                    )
                }
            }
            is KycEvent.ContactUsClick -> {
                next(KycUSF.ContactUs)
            }
            is KycEvent.SdkError -> {
                next(
                    state.copy(errorCode = event.errorCode.toString()),
                    KycUSF.ShowFailed(
                        ResourceManager.getInstance().getString(R.string.rp_owner_details_init_failed_title),
                        event.message,
                        ResourceManager.getInstance().getString(R.string.rp_dismiss),
                        if(state.isSkipJourneyEnabled){
                            ResourceManager.getInstance().getString(R.string.rp_skip)
                        }else{
                            null
                        }
                    )
                )
            }
            is KycEvent.ShowBackPressConfirmationDialog -> {
                next(
                    KycUSF.ShowBackPressConfirmationDialog
                )
            }
            is KycEvent.BackPressConfirmed, KycEvent.ProgressDialogSecondaryButtonClick -> {
                next(KycUSF.CloseKyc)
            }
            is KycEvent.UpdateSkipFlag -> {
                next(state.copy(isSkipJourneyEnabled = event.isSkipEnabled))
            }
            is KycEvent.ShowKycSateDialog -> {
                next(
                    KycUSF.ShowKycStateDialog(event.drawable, event.icon, event.title, event.subTitle, event.primaryButton, event.secondaryButton)
                )
            }
            is KycEvent.MultiChoiceInputClick -> {
                val options = event.options.map { ItemDialogBottomSheet(it, it, "") }
                next(
                    KycUSF.OpenMultiChoiceOptionSelection(
                        event.workFlow,
                        event.inputName,
                        event.title,
                        event.subTitle,
                        options,
                        event.jsonObject
                    )
                )
            }
            is KycEvent.MultiChoiceOptionSelected -> {
                if(!event.value.isNullOrEmpty()){
                    event.jsonObject.addProperty(event.inputName.name, event.value)
                    if(event.value == state.nonBusinessEnumString){
                        next(KycUSF.ShowNonBusinessConfirmationDialog(
                            ResourceManager.getInstance().getDrawable(R.color.rp_orange_2),
                            ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle,
                                ResourceManager.getInstance().getColor(R.color.rp_grey_6)),
                            ResourceManager.getInstance().getString(R.string.rp_are_you_sure),
                            ResourceManager.getInstance().getString(R.string.rp_providing_business_proof_ensures_fast_and_seamless_settlements),
                            ResourceManager.getInstance().getString(R.string.rp_upload_document),
                            ResourceManager.getInstance().getString(R.string.rp_i_dont_have_a_business),
                            event.workFlow,
                            event.jsonObject
                        ))
                    }else{
                        val nextInput = event.workFlow.input.find { it.priority > event.inputName.priority }
                        if(nextInput != null){
                            next(KycUSF.TriggerNextInputAction(event.workFlow, nextInput, event.jsonObject))
                        }else{
                            next(
                                KycASF.SubmitItem(event.workFlow, event.jsonObject),
                                KycUSF.ShowProgress(getProgressTitle(event.workFlow), getProgressSubTitle(event.workFlow))
                            )
                        }
                    }
                }else{
                    noChange()
                }
            }
            is KycEvent.UploadDocumentClick -> {
                next(KycUSF.ShowUploadDocumentDialog(
                    event.workFlow,
                    event.inputName,
                    event.title,
                    event.subTitle,
                    event.allowedExtension,
                    event.sizeLimit,
                    event.jsonObject))
            }
            is KycEvent.DocumentSelected -> {
                val sizeLimit = event.inputName.meta?.size_limit.int()
                if(sizeLimit > 0 && event.fileSize > 0 && event.fileSize > sizeLimit){
                    next(
                        KycUSF.ShowFailed(
                            ResourceManager.getInstance().getString(R.string.rp_file_size_is_too_big),
                            ResourceManager.getInstance().getString(R.string.rp_please_upload_again_with_the_file_size_less_than, sizeLimit),
                            ResourceManager.getInstance().getString(R.string.rp_retry),
                            null
                        )
                    )
                }else{
                    val progressMessage = ResourceManager.getInstance().getString(R.string.rp_please_wait_uploading)
                    next(
                        KycASF.UploadDocument(event.workFlow,
                            event.inputName,
                            event.url,
                            event.jsonObject,
                            event.fileSize),
                        KycUSF.ShowProgress(progressMessage, "")
                    )
                }
            }
            is KycEvent.UploadDocumentSuccessful -> {
                event.jsonObject.addProperty(event.inputName.name, event.url)
                val nextInput = event.workFlow.input.find { it.priority > event.inputName.priority }
                if(nextInput != null){
                    next(KycUSF.TriggerNextInputAction(event.workFlow, nextInput, event.jsonObject))
                }else{
                    next(
                        KycASF.SubmitItem(event.workFlow, event.jsonObject),
                        KycUSF.ShowProgress(getProgressTitle(event.workFlow), getProgressSubTitle(event.workFlow))
                    )
                }
            }
            is KycEvent.UploadDocumentFailed -> {
                next(KycUSF.ShowFailed(
                    ResourceManager.getInstance().getString(R.string.rp_document_upload_failed),
                    ResourceManager.getInstance().getString(R.string.rp_please_try_again),
                    ResourceManager.getInstance().getString(R.string.rp_retry),
                    null))
            }
        }
    }

    private fun getCurrentKycItem(kyc: Kyc): KycWorkFlow? {
        val kycItems = kyc.workflow.sortedBy { it.priority }.filter { it.status !in listOf(KycItemState.Completed, KycItemState.UnderReview) }
        return if (kycItems.isEmpty()) {
            null
        } else {
            kycItems.first()
        }
    }

    private fun getProgressSubTitle(kycItem: KycWorkFlow): String {
        val id = when(kycItem.name) {
            KycWorkFlowName.BusinessProof -> R.string.rp_pan_verification_in_progress_business_proof
            KycWorkFlowName.PanDetailsVerification -> R.string.rp_pan_verification_in_progress_subtitle
            KycWorkFlowName.BankVerification -> R.string.rp_settlement_bank_account_verification_in_progress_subtitle
            KycWorkFlowName.SdkWorkFlow -> R.string.rp_owner_verification_in_progress_subtitle
        }
        return ResourceManager.getInstance().getString(id)
    }

    private fun getProgressTitle(kycItem: KycWorkFlow): String {
        val id = when(kycItem.name) {
            KycWorkFlowName.BusinessProof -> R.string.rp_business_proof_verification_in_progress_title
            KycWorkFlowName.PanDetailsVerification -> R.string.rp_pan_verification_in_progress_title
            KycWorkFlowName.BankVerification -> R.string.rp_settlement_bank_account_verification_in_progress_title
            KycWorkFlowName.SdkWorkFlow -> R.string.rp_owner_details_verification_in_progress_title
        }
        return ResourceManager.getInstance().getString(id)
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: KycASF,
        dispatchEvent: (KycEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is KycASF.Load -> {
                kycUseCase.getKyc().collectIn(viewModelScope) {
                    val kycType = withContext(Dispatchers.IO){
                         propertyUseCase.getProperty(KycDataStore.KYC_TYPE)?.value ?: ""
                    }
                    dispatchEvent(KycEvent.KycLoaded(it, kycType))
                }
            }
            is KycASF.Refresh -> {
                kycUseCase.fetchKyc(propertyUseCase)
            }
            is KycASF.SubmitItem -> {
                delay(sideEffect.delay)
                when(val outcome = kycUseCase.submitKycItem(sideEffect.kycItem, sideEffect.jsonObject)) {
                    is Outcome.Success -> {
                        val kycItem = outcome.data.workflow.find { it.name == sideEffect.kycItem.name }
                        if (kycItem?.status == KycItemState.Rejected && kycItem.error != null) {
                            dispatchEvent(KycEvent.SubmissionFailed(sideEffect.kycItem, kycItem.error.code, kycItem.error.message))
                        } else {
                            dispatchEvent(KycEvent.SubmissionSuccess(sideEffect.kycItem))
                        }
                    }
                    is Outcome.Error -> {
                        dispatchEvent(KycEvent.SubmissionFailed(sideEffect.kycItem, outcome.error.code.orEmpty(), outcome.error.message.orEmpty()))
                    }
                }
            }
            is KycASF.InitItem -> {
                when(val outcome = kycUseCase.initKycItem(sideEffect.kycItem)) {
                    is Outcome.Success -> {
                        val updatedKycItem = outcome.data.workflow.find { it.name == sideEffect.kycItem.name }
                        dispatchEvent(KycEvent.InitSuccess(updatedKycItem))
                    }
                    is Outcome.Error -> {
                        dispatchEvent(KycEvent.InitFailed(sideEffect.kycItem, outcome.error.code.orEmpty(), outcome.error.message.orEmpty()))
                    }
                }
            }
            is KycASF.UploadDocument -> {
                when(val result = imageUseCase.uploadImage( "KYB_BUSINESS_PROOF",
                    sideEffect.fileSize.toString(), Uri.parse(sideEffect.uri))){
                    is Outcome.Success -> {
                        val url = result.data
                        sideEffect.jsonObject.addProperty(sideEffect.inputName.name, url)
                        when(val result2 = kycUseCase.submitKycItem(sideEffect.workFlow, sideEffect.jsonObject)){
                            is Outcome.Success -> {
                                val updatedWorkFlow = result2.data.workflow.find { it.name == sideEffect.workFlow.name } ?: sideEffect.workFlow
                                dispatchEvent(KycEvent.UploadDocumentSuccessful(
                                    updatedWorkFlow,
                                    sideEffect.inputName,
                                    url,
                                    sideEffect.jsonObject))
                            }
                            is Outcome.Error -> {
                                dispatchEvent(KycEvent.UploadDocumentFailed(result2.error.message.orEmpty()))
                            }
                        }
                    }
                    is Outcome.Error -> {
                        dispatchEvent(KycEvent.UploadDocumentFailed(result.error.message.orEmpty()))
                    }
                }
            }
        }
    }

    companion object{
        const val PAN_KEY = "pan"
        const val PAN_NAME_KEY = "pan_name"
        const val BUSINESS_OWNER_NAME_KEY = "business_owner_name"
        const val BUSINESS_PROOF_SELECTED = "business_proof_selected"
        const val BUSINESS_REGISTRATION_NUMBER = "reg_no"
        const val BUSINESS_IDENTITY_SELECTED = "document_type"
        const val BUSINESS_IDENTITY_NUMBER = "document_id"

        fun getFirstActinEvent(kycWorkFlow: KycWorkFlow, jsonObject: JsonObject): KycEvent?{
            val firstInput = kycWorkFlow.input.sortedBy { it.priority }[0]
            return getActionEvent(kycWorkFlow, firstInput, jsonObject)
        }

        fun getActionEvent(kycWorkFlow: KycWorkFlow, inputMeta: KycItemInputMeta?, jsonObject: JsonObject): KycEvent? {
            return when(inputMeta?.type){
                KycInputTypeEnum.MultiChoice.value -> {
                    val options = inputMeta.meta?.options
                    if(!options.isNullOrEmpty() && options.size > 1) {
                        KycEvent.MultiChoiceInputClick(
                            kycWorkFlow,
                            inputMeta,
                            inputMeta.meta.title ?: "",
                            inputMeta.meta.sub_title,
                            options,
                            jsonObject
                        )
                    }else{
                        null
                    }
                }
                KycInputTypeEnum.Document.value -> {
                    KycEvent.UploadDocumentClick(
                        kycWorkFlow,
                        inputMeta,
                        inputMeta.meta?.title ?: "",
                        inputMeta.meta?.sub_title ?: "",
                        inputMeta.meta?.allowed_extension ?: emptyList(),
                        inputMeta.meta?.size_limit,
                        jsonObject)
                }
                else -> {
                    null
                }
            }
        }

    }
}
