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
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderStateEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailState
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel.ProductOrderStateUi
import kotlin.math.roundToInt

internal class ProductOrderDetailUM(val dispatchEvent: (ProductOrderDetailEvent) -> Unit) : BaseMainUM() {

    val iconDrawable = ObservableField<Drawable>()
    val titleText = ObservableField<String>()
    val stateColor = ObservableInt()
    val quantityText = ObservableField<String>()
    val amount = ObservableField<SpannableString>()
    val amountTextColor = ObservableField<ColorStateList>(ResourceManager.getInstance().getColorStateList(
        R.color.rp_grey_1))
    val paidBy = ObservableField<String>()
    val installmentUtr = ObservableField<String>()
    val utr = ObservableField<String>()
    val utrCopyVisibility = ObservableInt()

    val refreshVisibility = ObservableBoolean()

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
            refreshVisibility.set(state.productOrder?.state in arrayOf(ProductOrderStateEnum.Created.value, ProductOrderStateEnum.InProgress.value))
            val backgroundColor = ProductOrderStateUi.get(ProductOrderStateEnum.get(order.state))
            stateColor.set(backgroundColor.background)
            toolbarBackground.set(ResourceManager.getInstance().getDrawable(stateColor.get()))
            toolbarTitleString.set(ResourceManager.getInstance().getString(backgroundColor.text))
            toolbarSubtitleString.set(DateUtils.getDate(order.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))

            val quantity = state.productOrder?.benefit?.sp.double().roundToInt()
            when(state.productType){
                ProductTypeEnum.Mandate.value -> {
                    iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_rocket_pay_small))
                    setTitle(order, ResourceManager.getInstance().getString(R.string.rp_mandate))
                    quantityText.set(if(quantity > 1){
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_mandates)}"
                    }else{
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_mandate)}"
                    })
                }
                ProductTypeEnum.Installment.value -> {
                    iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_rocket_pay_small))
                    setTitle(order, ResourceManager.getInstance().getString(R.string.rp_token))
                    quantityText.set(if(quantity > 1){
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_tokens)}"
                    }else{
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_token)}"
                    })
                }
                else -> {
                    iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_super_key))
                    setTitle(order, ResourceManager.getInstance().getString(R.string.rp_key))
                    quantityText.set(if(quantity > 1){
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_rocketpay_keys)}"
                    }else{
                        "$quantity ${ResourceManager.getInstance().getString(R.string.rp_rocketpay_key)}"
                    })
                }
            }

            setAmount(order)

            installmentUtr.set(order.id.ifNullOrEmpty("-"))
            if (order.meta?.utr.isNullOrEmpty()) {
                utr.set(ResourceManager.getInstance().getString(R.string.rp_utr_missing_msg))
                utrCopyVisibility.set(View.GONE)
            } else {
                utr.set(order.meta?.utr)
                utrCopyVisibility.set(View.VISIBLE)
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
}
