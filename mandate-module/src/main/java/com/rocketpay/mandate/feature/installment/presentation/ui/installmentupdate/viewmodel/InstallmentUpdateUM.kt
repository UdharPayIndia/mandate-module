package com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class InstallmentUpdateUM(private val dispatchEvent: (InstallmentUpdateEvent) -> Unit) : BaseMainUM() {

    val comment = ObservableField<String>()
    val cashDrawable = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
    val onlineDrawable = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(InstallmentUpdateEvent.CloseProgressDialog)
    })

    fun onCashClicked(){
        dispatchEvent(InstallmentUpdateEvent.UpdatePaymentMode(PaymentMode.Offline))
    }

    fun onOnlineClicked(){
        dispatchEvent(InstallmentUpdateEvent.UpdatePaymentMode(PaymentMode.Online))
    }

    fun onCommentChanged(str: CharSequence){
        dispatchEvent(InstallmentUpdateEvent.ReasonChanged(str.toString()))
    }

    fun onCancelClick(){
        dispatchEvent(InstallmentUpdateEvent.CloseClick)
    }

    fun onSaveClick(){
        dispatchEvent(InstallmentUpdateEvent.SaveClick)
    }

    fun handleState(state: InstallmentUpdateState) {
        when(state.mode){
            PaymentMode.Online -> {
                cashDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                onlineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
            }
            PaymentMode.Offline -> {
                cashDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                onlineDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty, ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
            }

            else -> {}
        }
        comment.set(state.reason)
    }

}