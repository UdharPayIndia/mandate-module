package com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderStateEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListEvent

internal class ItemProductOrderListVM (
    private val productOrder: ProductOrder,
    val itemClick: (ProductOrderListEvent) -> Unit,
    val position: Int,
    val productType: String
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val iconDrawable = ObservableField<Drawable>()
    val titleText = ObservableField<String>()
    val createdAtText = ObservableField<String>()
    val amountText = ObservableField<String>()
    val statusText = ObservableField<String>()
    val statusTextColor = ObservableField<ColorStateList>(ResourceManager.getInstance().getColorStateList(
        R.color.rp_grey_3))

    init {
        val quantity = productOrder.benefit?.sp.double().toLong()
        if(productOrder.orderType in arrayOf(
                ProductOrderTypeEnum.Cashback.value,
                ProductOrderTypeEnum.Purchase.value,
                ProductOrderTypeEnum.PurchaseRefund.value,
                ProductOrderTypeEnum.Sale.value)
            ){
            iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_add))
        }else {
            iconDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_minus))
        }
        titleText.set(when(productType){
            ProductTypeEnum.Mandate.value -> {
                if(quantity > 1){
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_mandates)}"
                }else{
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_mandate)}"
                }
            }
            ProductTypeEnum.Installment.value -> {
                if(quantity > 1){
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_tokens)}"
                }else{
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_token)}"
                }
            }
            else -> {
                if(quantity > 1){
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_keys)}"
                }else{
                    "$quantity ${ResourceManager.getInstance().getString(R.string.rp_key)}"
                }
            }
        })

        if(productOrder.orderType in arrayOf(ProductOrderTypeEnum.Redeem.value)){
            amountText.set("")
            statusText.set(ResourceManager.getInstance().getString(R.string.rp_redeemed))
            statusTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
            createdAtText.set(DateUtils.getDate(productOrder.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
        }else if(productOrder.orderType in arrayOf(ProductOrderTypeEnum.Cashback.value)){
            amountText.set("")
            statusText.set(ResourceManager.getInstance().getString(R.string.rp_free))
            statusTextColor.set(ResourceManager.getInstance().getColorStateList(R.color.rp_green_2))
            createdAtText.set(DateUtils.getDate(productOrder.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
        } else{
            val planStatusUi =
                ProductOrderStateUi.Companion.get(ProductOrderStateEnum.get(productOrder.state))
            if(planStatusUi != ProductOrderStateUi.Successful){
                statusText.set(ResourceManager.getInstance().getString(planStatusUi.text))
            }else{
                statusText.set("")
            }
            statusTextColor.set(ResourceManager.getInstance().getColorStateList(planStatusUi.background))
            if(!productOrder.paymentOrderId.isNullOrEmpty()){
                if(productOrder.price?.sp.double() > 0){
                    amountText.set(AmountUtils.format(productOrder.price?.sp.double()))
                }else{
                    amountText.set("NA")
                }
                createdAtText.set(DateUtils.getDate(productOrder.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT))
            }else{
                amountText.set("NA")
                createdAtText.set(DateUtils.getDate(productOrder.createdAt, DateUtils.DOT_DATE_AND_TIME_FORMAT_WITH_TEXT)  + " \u2022 " + ResourceManager.getInstance().getString(R.string.rp_distributor))
            }
        }
    }

    fun onItemClick() {
        itemClick(ProductOrderListEvent.ProductOrderClick(productOrder))
    }
}