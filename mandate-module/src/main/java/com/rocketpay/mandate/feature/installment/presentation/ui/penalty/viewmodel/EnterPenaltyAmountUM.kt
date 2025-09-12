package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.main.init.MandateManager
import kotlin.text.toDouble

internal class EnterPenaltyAmountUM (private val dispatchEvent: (EnterPenaltyAmountEvent) -> Unit) : BaseMainUM() {

    val penaltyAmount = ObservableField("")
    val penaltyAmountError = ObservableField<String>()

    val isEnabled = ObservableBoolean()
    val penaltyChargedByRocketPayLabel = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_rocketpay_charges,
        MandateManager.getInstance().getAppName()))
    val penaltyChargedByRocketPay = ObservableField<String>()
    val penaltyChargedByMerchant = ObservableField<String>()

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

        val totalPenaltyAmount = AmountUtils.stringToDouble(state.penaltyAmount)
        val rocketPayPortion = (state.minimumAmount -1).toDouble()
        val merchantPortion = totalPenaltyAmount - rocketPayPortion
        penaltyChargedByRocketPay.set(AmountUtils.format(rocketPayPortion))
        if(merchantPortion > 0){
            penaltyChargedByMerchant.set(AmountUtils.format(merchantPortion))
        }else{
            penaltyChargedByMerchant.set(AmountUtils.format(0.0))
        }
    }

    fun updatePenaltyAmount(str: CharSequence){
        dispatchEvent(EnterPenaltyAmountEvent.UpdatePenaltyAmount(str.toString()))
    }
    
    fun onSubmitClick(){
        dispatchEvent(EnterPenaltyAmountEvent.SubmitPenalty)
    }
}
