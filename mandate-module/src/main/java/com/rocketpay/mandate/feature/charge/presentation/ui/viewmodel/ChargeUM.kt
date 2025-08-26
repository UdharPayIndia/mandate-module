package com.rocketpay.mandate.feature.charge.presentation.ui.viewmodel

import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeEvent
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeState
import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFlowType
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.makeBold
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.strike
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ChargeUM(private val dispatchEvent: (ChargeEvent) -> Unit) : BaseMainUM() {

    val businessFlowBaseVisibility = ObservableInt(View.GONE)
    val customerFlowBaseVisibility = ObservableInt(View.GONE)
    val customerFlowTitle = ObservableField<String>(ResourceManager.getInstance().getString(R.string.rp_customer_bank_charge_subtitle,
        BuildConfig.APP_NAME))
    val autopayBaseVisibility = ObservableInt(View.GONE)
    val isCashFreeEnabled = ObservableBoolean(false)

    val serviceChargeDetail = ObservableField<SpannableString>()
    val serviceChargeHelperText = ObservableField<String>()

    fun onGeneratePaymentLinkClick() {
        dispatchEvent(ChargeEvent.DismissClick)
    }

    fun handleState(state: ChargeState) {
        val offerCharge = state.charge?.offerCharge.toString()
        val actualCharge = state.charge?.actualCharge.toString()
        val message = ResourceManager.getInstance().getString(R.string.rp_service_charge_detail_percentage, actualCharge, offerCharge)
            .getSpannable()
            .makeBold(offerCharge)
            .strike(actualCharge)
        serviceChargeDetail.set(message)

        serviceChargeHelperText.set(ResourceManager.getInstance().getString(R.string.rp_service_charge_subtitle, actualCharge))

        when(state.flowType){
            ChargeFlowType.AutoPay -> {
                autopayBaseVisibility.set(View.VISIBLE)
                customerFlowBaseVisibility.set(View.GONE)
                businessFlowBaseVisibility.set(View.GONE)
                isCashFreeEnabled.set(state.isCashFreeEnabled)
            }
            ChargeFlowType.Customer -> {
                customerFlowBaseVisibility.set(View.VISIBLE)
                autopayBaseVisibility.set(View.GONE)
                businessFlowBaseVisibility.set(View.GONE)
            }
            ChargeFlowType.Business -> {
                businessFlowBaseVisibility.set(View.VISIBLE)
                autopayBaseVisibility.set(View.GONE)
                customerFlowBaseVisibility.set(View.GONE)
            }
        }
    }
}
