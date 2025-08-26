package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.viewmodel

import android.text.SpannableString
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailState
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.ifNullOrEmpty
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextSize
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class SettlementDetailUM (private val dispatchEvent: (SettlementDetailEvent) -> Unit) : BaseMainUM() {

    val settledAmount = ObservableField<SpannableString>()
    val settlementSummary = ObservableField<String>()

    val utrNumber = ObservableField<String>()
    val settlementTime = ObservableField<String>()
    val settlementAccountNumber = ObservableField<String>()
    val installmentVisibility = ObservableBoolean()
    val errorVisibility = ObservableBoolean()
    val errorMessage = ObservableField<String>()
    val installmentCountText = ObservableField<String>()

    fun handleState(state: SettlementDetailState) {
        if(state.paymentOrder?.getSettlementAmount() != null) {
            settledAmount.set(
                AmountUtils.format(state.paymentOrder?.getSettlementAmount().double())
                    .getSpannable().setTextSize(AmountUtils.CURRENCY_SYMBOL, 0.5f)
            )
        }

        utrNumber.set(state.paymentOrder?.meta?.utr.ifNullOrEmpty("-"))

        if(state.paymentOrder?.createdAt != null) {
            settlementTime.set(
                DateUtils.getDate(
                    state.paymentOrder?.createdAt ?: 0,
                    DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT
                )
            )
        }
        val name = state.paymentOrder?.meta?.instrumentDetails?.accountHolderNameAtBank.toCapitalise()
        val bankName = state.paymentOrder?.meta?.instrumentDetails?.bankCode.ifNullOrEmpty("")
        val accountNumber = state.paymentOrder?.meta?.instrumentDetails?.accountNumber.ifNullOrEmpty("")
        settlementAccountNumber.set(if(!name.isNullOrEmpty()){
            if(!bankName.isNullOrEmpty()){
                "$name \u2022 $bankName $accountNumber"
            }else{
                "$name \u2022 $accountNumber"
            }
        }else{
            if(!bankName.isNullOrEmpty()){
                "$bankName $accountNumber"
            }else{
                accountNumber
            }
        })


        val installmentsSize = state.installments.size + state.refundedInstallments.size

        if(installmentsSize > 0){
            settlementSummary.set("$installmentsSize ${ResourceManager.getInstance().getString(R.string.rp_installments)}")
            installmentCountText.set("${ResourceManager.getInstance().getString(R.string.rp_total_installments)} (${installmentsSize})")
            installmentVisibility.set(installmentsSize > 0)
            errorVisibility.set(false)
        }else if(installmentsSize == 0 && !state.error.isNullOrEmpty()){
            errorVisibility.set(true)
            errorMessage.set(state.error.ifNullOrEmpty(ResourceManager.getInstance().getString(R.string.rp_ivr_invoice_something_went_wrong)))
            installmentVisibility.set(false)
        }else{
            installmentVisibility.set(false)
            errorVisibility.set(false)
        }


    }

    fun onUtrClick(){
        dispatchEvent(
            SettlementDetailEvent.UtrCopyClick(
                ResourceManager.getInstance().getString(
                    R.string.rp_utr_number_copied), utrNumber.get() ?: ""))
    }

    fun onRetryClick(){
        dispatchEvent(SettlementDetailEvent.RetryClick)
    }
}