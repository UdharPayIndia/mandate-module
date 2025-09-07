package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.main.init.MandateManager

internal class ItemPenaltyVM(
    private val onPenaltyStateChange: (Boolean, Boolean?) -> Unit,
    private val onPenaltyAmountChange: (String) -> Unit
) {

    val penaltyEnableErrorText = ObservableField<String>()
    val penaltyAmount = ObservableField<String>()
    val penaltyAmountError = ObservableField<String>()
    val noPenaltyDrawable = ObservableField<Drawable>(
        ResourceManager.getInstance().getDrawable(
            R.drawable.rp_ic_radio_empty,
            ResourceManager.getInstance().getColor(R.color.rp_blue_2)
        )
    )
    val manualPenaltyDrawable = ObservableField<Drawable>(
        ResourceManager.getInstance().getDrawable(
            R.drawable.rp_ic_radio_empty,
            ResourceManager.getInstance().getColor(R.color.rp_blue_2)
        )
    )
    val autoPenaltyDrawable = ObservableField<Drawable>(
        ResourceManager.getInstance().getDrawable(
            R.drawable.rp_ic_radio_empty,
            ResourceManager.getInstance().getColor(R.color.rp_blue_2)
        )
    )
    val manualPenaltyVisibility = ObservableBoolean()
    val autoPenaltyVisibility = ObservableBoolean()

    val penaltyTextByRocketpay = ObservableField<String>(
        ResourceManager.getInstance().getString(R.string.rp_rocketpay_charges,
            MandateManager.getInstance().getAppName()))
    val penaltyChargedByRocketPay = ObservableField<String>()
    val penaltyChargedByMerchant = ObservableField<String>()

    fun setData(
        allowPenalty: Boolean,
        isAuto: Boolean?,
        penaltyEnableError: String,
        penaltyAmount1: String,
        penaltyAmountError1: String?,
        minimumPenaltyAmount: Int
    ){
        when{
            allowPenalty && isAuto == true -> {
                noPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                manualPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                autoPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))

                penaltyAmount.set(penaltyAmount1)
                penaltyAmountError.set(penaltyAmountError1)

                manualPenaltyVisibility.set(false)
                autoPenaltyVisibility.set(true)
            }
            allowPenalty && isAuto == false -> {
                noPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                manualPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                autoPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                manualPenaltyVisibility.set(true)
                autoPenaltyVisibility.set(false)
            }
            else -> {
                noPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_filled,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                manualPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                autoPenaltyDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_radio_empty,
                    ResourceManager.getInstance().getColor(R.color.rp_blue_2)))
                manualPenaltyVisibility.set(false)
                autoPenaltyVisibility.set(false)
                penaltyEnableErrorText.set(penaltyEnableError)
            }
        }

        val totalPenaltyAmount = AmountUtils.stringToDouble(penaltyAmount1)
        val rocketPayCharges = (minimumPenaltyAmount - 1).toDouble()
        penaltyChargedByRocketPay.set(AmountUtils.format(rocketPayCharges))
        if(totalPenaltyAmount - rocketPayCharges > 0){
            penaltyChargedByMerchant.set(AmountUtils.format(totalPenaltyAmount - rocketPayCharges))
        }else{
            penaltyChargedByMerchant.set(AmountUtils.format(0.0))
        }
    }

    fun onNoPenaltyClick(){
        onPenaltyStateChange.invoke(
            false, null
        )
    }

    fun onManualPenaltyClick(){
        onPenaltyStateChange.invoke(
            true,
            false
        )
    }

    fun onAutoPenaltyClick(){
        onPenaltyStateChange.invoke(
            true,
            true
        )
    }

    fun onPenaltyAmountChanged(str: CharSequence){
        onPenaltyAmountChange.invoke(str.toString())
    }
}