package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemMandateDetailVM(
    val installment: Installment,
    nextInstallmentId: String?,
    val itemClick: (MandateDetailEvent) -> Unit,
    val manualMandate: Boolean
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val background = ObservableField<Drawable>()
    val forwardIcon = ObservableField<Drawable>()
    val forwardIconVisibility = ObservableInt()

    val installmentDetail = ObservableField<String>()
    val date = ObservableField<String>()
    val amount = ObservableField<String>()
    val installmentState = ObservableField<String>()
    val installmentStateIcon = ObservableField<Drawable>()
    val installmentAmountColor = ObservableInt()

    fun onItemClick() {
        itemClick(MandateDetailEvent.ItemClick(installment))
    }

    init {
        installmentDetail.set(ResourceManager.getInstance().getString(R.string.rp_installment_title, installment.serialNumber))
        date.set(DateUtils.getDate(installment.dueDate, DateUtils.MONTH_DATE_FORMAT))

        val formatAmount = AmountUtils.format(installment.amountUI)
        amount.set(formatAmount)

        val installmentStateUi = installment.getInstallmentStatusUi(manualMandate)
        installmentState.set(ResourceManager.getInstance().getString(installmentStateUi.text))
        installmentStateIcon.set(ResourceManager.getInstance().getDrawable(installmentStateUi.icon, ResourceManager.getInstance().getColor(installmentStateUi.color)))

        when(installment.state) {
            InstallmentState.CollectionSuccess, InstallmentState.SettlementInitiated,
            InstallmentState.SettlementFailed, InstallmentState.SettlementSuccess -> {
                installmentAmountColor.set(ResourceManager.getInstance().getColor(installmentStateUi.color))
            }
            else -> {
                installmentAmountColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_1))
            }
        }

        if (nextInstallmentId != null && nextInstallmentId == installment.id) {
            forwardIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_forward, ResourceManager.getInstance().getColor(R.color.rp_blue_3)))
            background.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_item_active))
        } else {
            forwardIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_forward, ResourceManager.getInstance().getColor(R.color.rp_grey_3)))
            background.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_item))
        }

        forwardIconVisibility.set(View.VISIBLE)
    }
}
