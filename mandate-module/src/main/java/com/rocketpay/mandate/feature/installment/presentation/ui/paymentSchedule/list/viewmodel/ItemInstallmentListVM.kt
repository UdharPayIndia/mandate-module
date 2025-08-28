package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListEvent
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.MandateStateUi
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemInstallmentListVM(
    val installment: InstallmentWithMandateEntity,
    val itemClick: (PaymentTrackerListEvent) -> Unit,
    private val hideTag: Boolean
): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val headerTitle = ObservableField<String>()
    val headerUrl = ObservableField<String>()
    val bodyTitle = ObservableField<String>()
    val bodySubtitle = ObservableField<String>()
    val footerTitle = ObservableField<String>()
    val footerDrawable = ObservableField<Drawable>()
    val footerTitleTextColor = ObservableField<ColorStateList>(ResourceManager.getInstance().getColorStateList(R.color.rp_grey_1))

    init {
        headerTitle.set(installment.customerName)
        bodyTitle.set("${installment.customerName} \u2022 #${installment.installment.serialNumber}")
        if(hideTag){
            if(installment.installment.status == InstallmentState.SettlementSuccess.value){
                bodySubtitle.set("${installment.paidInstallment}/${installment.noOfInstallment} ${ResourceManager.getInstance().getString(R.string.rp_installment)}")
            }else{
                bodySubtitle.set("${installment.paidInstallment}/${installment.noOfInstallment} ${ResourceManager.getInstance().getString(R.string.rp_installment)}" +
                        " \u2022 ${ResourceManager.getInstance().getString(R.string.rp_yet_to_settle)}")
            }
        }else {
            if(installment.paymentMethod != PaymentMethod.Manual.value){
                bodySubtitle.set("${installment.paidInstallment}/${installment.noOfInstallment} ${ResourceManager.getInstance().getString(R.string.rp_installment)}" +
                        " \u2022 ${ResourceManager.getInstance().getString(MandateStateUi.getMandateStateUi(
                            MandateState.get(installment.mandateState)).text)}")
            }else {
                bodySubtitle.set("${installment.paidInstallment}/${installment.noOfInstallment} ${ResourceManager.getInstance().getString(R.string.rp_installment)}")
            }
        }

        val totalAmount = AmountUtils.format(installment.installment.amountUI)
        footerTitle.set(totalAmount)

        if(installment.installment.paymentModeDetailEntity?.merchantCollected == true
            || installment.installment.status == InstallmentState.SettlementSuccess.value){
            footerDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_success))
            footerTitleTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
        }else if(installment.installment.status in arrayOf(InstallmentState.SettlementFailed.value,
                InstallmentState.SettlementInitiated.value, InstallmentState.CollectionSuccess.value)){
            footerDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_pending_clock))
            footerTitleTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_yellow_2))
        }else{
            footerDrawable.set(null)
            footerTitleTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_grey_1))
        }
    }

    fun onItemClick() {
        itemClick(PaymentTrackerListEvent.InstallmentClick(installment))
    }
}
