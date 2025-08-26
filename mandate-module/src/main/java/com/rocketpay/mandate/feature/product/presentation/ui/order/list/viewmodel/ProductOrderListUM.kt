package com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListState

internal class ProductOrderListUM(private val dispatchEvent: (ProductOrderListEvent) -> Unit) : BaseMainUM() {

    val emptyText = ObservableField<String>()
    val emptyStateVisibility = ObservableBoolean()

    val itemProductSummaryVM = ItemProductSummaryVM()

    fun onCloseClick() {
        dispatchEvent(ProductOrderListEvent.CloseClick)
    }

    fun handleState(state: ProductOrderListState) {
        itemProductSummaryVM.setData(state.productWallet)
        when(state.productType){
            ProductTypeEnum.Mandate.value -> {
                toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_mandate_history))
                emptyText.set(ResourceManager.getInstance().getString(R.string.rp_you_have_not_made_any_payment_for_mandates))
            }
            ProductTypeEnum.Installment.value -> {
                toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_token_history))
                emptyText.set(ResourceManager.getInstance().getString(R.string.rp_you_have_not_made_any_payment_for_tokens))
            }
            else ->{
                toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_key_history))
                emptyText.set(ResourceManager.getInstance().getString(R.string.rp_you_have_not_made_any_payment_for_keys))
            }
        }
        if(state.productOrderList.isNullOrEmpty()){
            emptyStateVisibility.set(true)
        }else{
            emptyStateVisibility.set(false)
        }
    }
}
