package com.rocketpay.mandate.feature.product.presentation.ui.order.detail.viewmodel

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getSpannable
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.ifNullOrEmpty
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.setTextSize
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.toCapitalise
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.MandateStateUi
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderStateEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailState
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel.ProductOrderStateUi
import kotlin.collections.get
import kotlin.math.roundToInt
import kotlin.text.format
import kotlin.text.get
import kotlin.text.set

internal class ProductOrderDetailUM(val dispatchEvent: (ProductOrderDetailEvent) -> Unit) : BaseMainUM() {

    val iconDrawable = ObservableField<Drawable>()
    val titleText = ObservableField<String>()
    val stateColor = ObservableInt()
    val quantityText = ObservableField<String>()
    val amount = ObservableField<SpannableString>()
    val amountTextColor = ObservableField<ColorStateList>(ResourceManager.getInstance().getColorStateList(
        R.color.rp_grey_1))
    val installmentUtr = ObservableField<String>()

    val refreshVisibility = ObservableBoolean()
    val isRedeemOrderType = ObservableBoolean()
    val referenceVisibility = ObservableBoolean()
    val referenceType = ObservableField<String>()
    val referenceNameText = ObservableField<String>()
    val referenceDateText = ObservableField<String>()
    val referenceAmountText = ObservableField<String>()
    val referenceInfoText = ObservableField<String>()
    val referenceInfoSubtitleColor = ObservableInt()
    val referenceInfoBackgroundDrawable = ObservableField<Drawable>()

    fun onRocketPayTransactionIdCopyClick() {
        dispatchEvent(
            ProductOrderDetailEvent.RocketPayTransactionIdCopyClick(
                ResourceManager.getInstance().getString(
                    R.string.copied),installmentUtr.get() ?: ""))
    }

    fun onActionRefreshClick(){
        dispatchEvent(ProductOrderDetailEvent.RefreshClick)
    }

    fun handleState(state: ProductOrderDetailState) {
        state.productOrder?.let { order ->

            if(state.productOrder?.orderType in arrayOf(ProductOrderTypeEnum.Redeem.value,
                    ProductOrderTypeEnum.RedeemRefund.value)){
                isRedeemOrderType.set(true)
                refreshVisibility.set(false)
                stateColor.set(R.color.rp_green_2)
                toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_green_2))
                if(state.productOrder?.orderType == ProductOrderTypeEnum.RedeemRefund.value
                    || state.productOrder?.state == ProductOrderStateEnum.Failed.value){
                    when(state.productType){
                        else ->{
                            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_payment_token_refunded))
                        }
                    }
                }else{
                    when(state.productType) {
                        else -> {
                            toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_payment_token_redeemed))
                        }
                    }
                }
                setReferenceDetails(state)
            }else {
                isRedeemOrderType.set(false)
                refreshVisibility.set(state.productOrder?.state
                        in arrayOf(ProductOrderStateEnum.Created.value, ProductOrderStateEnum.InProgress.value))
                val backgroundColor = ProductOrderStateUi.get(ProductOrderStateEnum.get(order.state))
                stateColor.set(backgroundColor.background)
                toolbarBackground.set(ResourceManager.getInstance().getDrawable(stateColor.get()))
                toolbarTitleString.set(ResourceManager.getInstance().getString(backgroundColor.text))
                refreshVisibility.set(false)
            }
            toolbarSubtitleString.set(DateUtils.getDate(order.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))

            val quantity = state.productOrder?.benefit?.sp.double().roundToInt()
            when(state.productType){
                else -> {
                    iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_token))
                    setTitle(order, ResourceManager.getInstance().getString(R.string.rp_token))
                    quantityText.set(if(quantity > 1){
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_tokens)}"
                    }else{
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_token)}"
                    })
                }
            }

            setAmount(order)

            installmentUtr.set(order.id.ifNullOrEmpty("-"))
        }
    }

    private fun setReferenceDetails(state: ProductOrderDetailState) {
        when(state.productType){
            else -> {
                referenceVisibility.set(state.mandate != null || state.installment !== null)
                if(state.mandate != null){
                    referenceType.set(ResourceManager.getInstance().getString(R.string.rp_mandate))
                    referenceNameText.set(state.mandate.customerDetail.name)

                    val startDate = DateUtils.getDate(state.mandate.createdAt, DateUtils.MONTH_DATE_FORMAT)
                    referenceDateText.set(ResourceManager.getInstance().getString(R.string.rp_mandate_created_on, startDate))

                    val totalAmount = AmountUtils.format(state.mandate.getMandateAmount())
                    val totalPaidAmount = AmountUtils.format(state.mandate.getMandatePaidAmount())
                    referenceAmountText.set(ResourceManager.getInstance().getString(R.string.rp_mandate_list_amount,
                        totalPaidAmount, totalAmount))

                    if(state.mandate.isDeleted){
                        referenceInfoText.set(ResourceManager.getInstance().getString(R.string.rp_deleted))
                        referenceInfoSubtitleColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                        val mandateLightColor = ResourceManager.getInstance().getColor(R.color.rp_grey_4)
                        referenceInfoBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_2, mandateLightColor))
                    }else{
                        val mandateStateUi = MandateStateUi.getMandateStateUi(state.mandate.state)
                        referenceInfoText.set(ResourceManager.getInstance().getString(mandateStateUi.text))
                        referenceInfoSubtitleColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                        val mandateLightColor = ResourceManager.getInstance().getColor(mandateStateUi.background)
                        referenceInfoBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_2, mandateLightColor))
                    }
                }else if(state.installment != null){
                    referenceType.set(ResourceManager.getInstance().getString(R.string.rp_installment))
                    referenceNameText.set(state.installment.customerName)

                    val installmentStateUi = state.installment.getInstallmentStatusUi(false)
                    referenceDateText.set(ResourceManager.getInstance().getString(installmentStateUi.text))

                    val totalAmount = AmountUtils.format(state.installment.amountUI)
                    referenceAmountText.set(totalAmount)

                    referenceInfoText.set(ResourceManager.getInstance().getString(R.string.rp_installment) + " #${state.installment.serialNumber}")
                    referenceInfoSubtitleColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))
                    val mandateLightColor = ResourceManager.getInstance().getColor(R.color.rp_grey_6)
                    referenceInfoBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_2,
                        mandateLightColor))
                }
            }
        }
    }


    private fun setTitle(productOrder: ProductOrder, productName: String){
        titleText.set(productName + " " + productOrder.orderType.toCapitalise())
    }

    private fun setAmount(productOrder: ProductOrder){
        if(productOrder.orderType in arrayOf(ProductOrderTypeEnum.Cashback.value)){
            amount.set(ResourceManager.getInstance().getString(R.string.rp_free).getSpannable())
            amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
        }else if(productOrder.orderType in arrayOf(ProductOrderTypeEnum.Redeem.value,
                ProductOrderTypeEnum.RedeemRefund.value)){
            amount.set(null)
            amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
        }else{
            if(!productOrder.paymentOrderId.isNullOrEmpty()){
                if(productOrder.price?.sp.double() > 0){
                    amount.set(AmountUtils.format(productOrder.price?.sp.double()).getSpannable())
                }else{
                    amount.set(AmountUtils.format(productOrder.price?.sp.double()).getSpannable())
                }
                amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_grey_1))
            }else{
                val string = ResourceManager.getInstance().getString(R.string.rp_from_distributor)
                amount.set(string.getSpannable().setTextSize(string, 0.75f))
                amountTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_grey_1))
            }
        }
    }

    fun onReferenceClick(){
        dispatchEvent(ProductOrderDetailEvent.ReferenceItemClick)
    }
}
