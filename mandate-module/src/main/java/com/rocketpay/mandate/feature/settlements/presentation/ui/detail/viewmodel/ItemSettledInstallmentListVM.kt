package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.viewmodel

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemSettledInstallmentListVM(
    val installment: Installment,
    val itemClick: (SettlementDetailEvent) -> Unit,
    val isRefunded: Boolean
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val nameText = ObservableField<String>()
    val dateText = ObservableField<String>()
    val amountText = ObservableField<String>()
    val amountTextColor = ObservableField<ColorStateList>(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
    val amountIcon = ObservableField<Drawable>(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_success))
    val serialNumberText = ObservableField<String>()

    init {
        nameText.set(installment.customerName)
        dateText.set(
            DateUtils.getDate(
                installment.dueDate,
                DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT
            )
        )
        val totalAmount = AmountUtils.format(installment.amountUI)
        amountText.set(totalAmount)
        serialNumberText.set(
            "${
                ResourceManager.getInstance().getString(R.string.rp_installment)
            } #${installment.serialNumber}"
        )

        if(isRefunded){
            amountIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_cash_refund))
            amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_orange_1))
        }else{
            amountIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_success))
            amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
        }
    }

    fun onItemClick() {
        itemClick(SettlementDetailEvent.InstallmentClick(installment))
    }

}
