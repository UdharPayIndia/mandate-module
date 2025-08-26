package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine

import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler

internal class KycAnalyticsHandler : BaseAnalyticsHandler<KycEvent, KycState>() {

    companion object {
        const val KYC_ITEM_SUBMIT_CLICK = "kyc_submit_click"
        const val KYC_ITEM_SUBMITTED = "kyc_submit_success"
        const val KYC_ITEM_FAILED = "kyc_submit_failed"
        const val KYC_ITEM_INITIATE_CLICK = "kyc_initiate_click"
        const val KYC_ITEM_INITIATED = "kyc_initiate_success"
        const val KYC_ITEM_INITIATED_FAILED = "kyc_initiate_failed"
    }

    override fun updateCommonEventParameter(state: KycState, paramBuilder: HashMap<String, Any>) {
        super.updateCommonEventParameter(state, paramBuilder)
        paramBuilder["flow_type"] = state.type ?: ""
    }

    override fun updateEventParameter(
        event: KycEvent,
        state: KycState,
        paramBuilder: HashMap<String, Any>
    ) {
        super.updateEventParameter(event, state, paramBuilder)
        when(event) {
            is KycEvent.KycLoaded -> {
                if (event.kyc != null) {
                } else {
                    event.name = null
                }
            }
            KycEvent.OwnerIdentityDetailSubmitted -> {
                event.name = "${KycWorkFlowName.SdkWorkFlow.value}_${KYC_ITEM_SUBMIT_CLICK}"
            }
            is KycEvent.InitItem -> {
                event.name = "${event.kycItem.name.value}_${KYC_ITEM_INITIATE_CLICK}"
            }
            is KycEvent.InitSuccess -> {
                event.name = "${event.kycItem?.name?.value}_${KYC_ITEM_INITIATED}"
            }
            is KycEvent.InitFailed -> {
                paramBuilder["errorCode"] = event.code
                event.name = "${event.kycItem.name.value}_${KYC_ITEM_INITIATED_FAILED}"
            }
            is KycEvent.SubmitItem -> {
                event.name = "${event.kycItem.name.value}_${KYC_ITEM_SUBMIT_CLICK}"
            }
            is KycEvent.SubmissionSuccess -> {
                event.name = "${event.kycItem.name.value}_${KYC_ITEM_SUBMITTED}"
            }
            is KycEvent.SubmissionFailed -> {
                paramBuilder["errorCode"] = event.code
                event.name = "${event.kycItem.name.value}_${KYC_ITEM_FAILED}"
            }
            is KycEvent.ProgressDialogPrimaryButtonClick, KycEvent.ProgressDialogSecondaryButtonClick -> {
                paramBuilder["errorCode"] = state.errorCode
            }
            is KycEvent.SdkError -> {
                paramBuilder["errorCode"] = event.errorCode.toString()
                paramBuilder["message"] = event.message
            }
            else -> {

            }
        }
    }
}
