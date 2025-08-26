package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM

internal class EnterPenaltyAmountUM (private val dispatchEvent: (EnterPenaltyAmountEvent) -> Unit) : BaseMainUM() {

    val penaltyAmount = ObservableField("")
    val penaltyAmountError = ObservableField<String>()

    val isEnabled = ObservableBoolean()

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(EnterPenaltyAmountEvent.CloseProgressDialog)
    })
    val confirmDialogVM = ProgressDialogVM(
        {
            when(it) {
                ProgressDialogStatus.Init -> dispatchEvent(EnterPenaltyAmountEvent.ChargePenaltyConfirmed)
                ProgressDialogStatus.Progress -> {
                    // No action required
                }
                ProgressDialogStatus.Success -> dispatchEvent(EnterPenaltyAmountEvent.ChargePenaltyDismiss)
                ProgressDialogStatus.Error -> dispatchEvent(EnterPenaltyAmountEvent.ChargePenaltyDismiss)
            }
        },
        {
            dispatchEvent(EnterPenaltyAmountEvent.ChargePenaltyDismiss)
        }
    )


    fun handleState(state: EnterPenaltyAmountState) {
        penaltyAmount.set(state.penaltyAmount)
        penaltyAmountError.set(state.penaltyAmountError)
        isEnabled.set(state.isEnabled)
    }

    fun updatePenaltyAmount(str: CharSequence){
        dispatchEvent(EnterPenaltyAmountEvent.UpdatePenaltyAmount(str.toString()))
    }
    
    fun onSubmitClick(){
        dispatchEvent(EnterPenaltyAmountEvent.SubmitPenalty)
    }
}
