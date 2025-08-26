package com.rocketpay.mandate.feature.product.presentation.ui.summary.viewmodel

import android.content.res.ColorStateList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogVM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryEvent
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryState
import kotlin.math.abs

internal class ProductSummaryUM(private val dispatchEvent: (ProductSummaryEvent) -> Unit) : BaseMainUM() {

    val balanceLabelText = ObservableField<String>()
    val balanceLabelTextColor = ObservableField<ColorStateList>(
        ResourceManager.getInstance().getColorStateList(

            R.color.rp_grey_3))

    val balanceTextColor = ObservableField<ColorStateList>(
        ResourceManager.getInstance().getColorStateList(
            R.color.rp_grey_1)
    )

    val balanceKeysText = ObservableField<String>("0")
    val historyButtonVisibility = ObservableBoolean()

    val progressDialogVM = ProgressDialogVM({
        dispatchEvent(ProductSummaryEvent.CloseLoading)
    })

    fun handleState(state: ProductSummaryState) {
        setKeysCount(state)

        historyButtonVisibility.set(true)

        val balance = state.productWallet?.outstanding.double()
        balanceKeysText.set(AmountUtils.format(balance, false))
    }

    private fun setKeysCount(state: ProductSummaryState){
        val balanceKeyCount = abs(state.productWallet?.outstanding.double())

        balanceLabelText.set(if(balanceKeyCount <= 0){
            balanceLabelTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_red_2))
            ResourceManager.getInstance().getString(R.string.rp_low_balance)
        }else{
            balanceLabelTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_grey_1))
            ResourceManager.getInstance().getString(R.string.rp_balance)
        })

    }

    fun onHistoryClick(){
        dispatchEvent(ProductSummaryEvent.HistoryClick)
    }
}