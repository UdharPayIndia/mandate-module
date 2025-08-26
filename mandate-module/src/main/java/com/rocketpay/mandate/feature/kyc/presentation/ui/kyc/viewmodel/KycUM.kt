package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.gson.JsonObject
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycState
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycStateMachine
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetconfirmation.BottomSheetConfirmationVM
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class KycUM(private val dispatchEvent: (KycEvent) -> Unit) : BaseMainUM() {

    var isBackPressDialogShown: Boolean = false
    var isBackPressConfirmed: Boolean = false
    var isErrorDialogShown: Boolean = false
    var isKycCompletedOrUnderReview: Boolean = false

    val emptyStateVisibility = ObservableInt()
    val illustration = ObservableField<Drawable>()
    val illustrationDescription = ObservableField<String>()

    var kycWorkFlow: KycWorkFlow? = null
    var kycInputName: KycItemInputMeta? = null
    var jsonObject: JsonObject = JsonObject()

    val progressDialogVM = ProgressDialogVM ({
        if(isErrorDialogShown){
            isErrorDialogShown = false
            dispatchEvent(KycEvent.ProgressDialogPrimaryButtonClick)
        }else if(isBackPressConfirmed){
            dispatchEvent(KycEvent.BackPressConfirmed)
        }else if(isBackPressDialogShown){
            isBackPressDialogShown = false
            dispatchEvent(KycEvent.BackPressSkipped)
        }else{
            dispatchEvent(KycEvent.ProgressDialogPrimaryButtonClick)
        }
    },{
        if(isErrorDialogShown){
            isErrorDialogShown = false
            isBackPressConfirmed = true
            dispatchEvent(KycEvent.ProgressDialogSecondaryButtonClick)
        }else if(isBackPressDialogShown || isBackPressConfirmed) {
            isBackPressDialogShown = false
            isBackPressConfirmed = true
            dispatchEvent(KycEvent.BackPressConfirmed)
        }else{
            dispatchEvent(KycEvent.ProgressDialogSecondaryButtonClick)
        }
    })
    val bottomSheetConfirmationVM = BottomSheetConfirmationVM(
        {
            kycWorkFlow?.let {
                val event = KycStateMachine.getFirstActinEvent(it, jsonObject)
                if (event != null) {
                    dispatchEvent(event)
                }
            }
        },
        {
            kycWorkFlow?.let {
                dispatchEvent(KycEvent.SubmitItem(it, jsonObject))
            }
        }
    )

    fun handleState(state: KycState) {
        isKycCompletedOrUnderReview = KycStateEnum.isInReviewOrCompleted(state.kyc?.state?.value)
        if(isKycCompletedOrUnderReview){
            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_your_kyc_details))
        }else{
            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_complete_kyc_registration_form))
        }
        if (state.kyc == null || state.kyc.workflow.isEmpty()) {
            emptyStateVisibility.set(View.VISIBLE)
        } else {
            emptyStateVisibility.set(View.GONE)
        }
    }
}
