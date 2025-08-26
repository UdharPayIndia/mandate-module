package com.rocketpay.mandate.feature.installment.presentation.ui.retry.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodState
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class SelectRetryPeriodUM (private val dispatchEvent: (SelectRetryPeriodEvent) -> Unit) : BaseMainUM() {


    val immediateDrawable = ObservableField<Drawable>()
    val selectDateDrawable = ObservableField<Drawable>()

    val retryOption = ObservableInt()
    val retryDate = ObservableField<String>()

    val isEnabled = ObservableBoolean()

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(SelectRetryPeriodEvent.CloseProgressDialog)
    })
    val confirmDialogVM = ProgressDialogVM(
        {
            when(it) {
                ProgressDialogStatus.Init -> dispatchEvent(SelectRetryPeriodEvent.RetryConfirmed)
                ProgressDialogStatus.Progress -> {
                    // No action required
                }
                ProgressDialogStatus.Success -> dispatchEvent(SelectRetryPeriodEvent.RetryDismiss)
                ProgressDialogStatus.Error -> dispatchEvent(SelectRetryPeriodEvent.RetryDismiss)
            }
        },
        {
            dispatchEvent(SelectRetryPeriodEvent.RetryDismiss)
        }
    )


    fun handleState(state: SelectRetryPeriodState) {
        retryOption.set(state.retryOption)
        isEnabled.set(state.isEnabled)
        when(state.retryOption){
            0 -> {
                immediateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
                selectDateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                retryDate.set(DateUtils.getDate(state.retryDate, DateUtils.MONTH_DATE_FORMAT))
            }
            1 -> {
                immediateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                selectDateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled))
                retryDate.set(DateUtils.getDate(state.retryDate, DateUtils.MONTH_DATE_FORMAT))
            }
            else -> {
                immediateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                selectDateDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty))
                retryDate.set(null)
            }
        }

    }

    fun onImmediateClick(){
        dispatchEvent(SelectRetryPeriodEvent.SelectImmediateDate)
    }

    fun onSelectDateClick(){
        dispatchEvent(SelectRetryPeriodEvent.SelectOtherDate)
    }

    fun onSubmitClick(){
        dispatchEvent(SelectRetryPeriodEvent.SubmitRetry)
    }
}
