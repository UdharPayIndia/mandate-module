package com.rocketpay.mandate.feature.product.presentation.ui.order.list.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel.ItemProductOrderListVM

internal class ProductOrderListAdapter: RecyclerViewAdapter() {

    lateinit var itemClick: (ProductOrderListEvent) -> Unit
    private var productType: String = ""

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_product_order_list_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemProductOrderListVM(
            list[position].any as ProductOrder,
            itemClick,
            position,
            productType
        )
    }

    fun swapData(items: List<ProductOrder>, productType: String) {
        this.productType = productType
        list.clear()
        list.addAll(items.sortedByDescending { it.createdAt }.map { RecyclerViewItem(VIEW_TYPE_ITEM, it) })
        notifyDataSetChanged()
    }
}
