package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.adapter

import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Coupon
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ItemCouponVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class CouponListAdapter  : RecyclerViewAdapter() {

    lateinit var itemClick: (MandateAddEvent) -> Unit
    lateinit var selectedCoupon: ObservableField<Coupon>

    companion object {
        const val VIEW_TYPE_ITEM = "VIEW_TYPE_ITEM"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            VIEW_TYPE_ITEM -> R.layout.item_coupon_rp
            else -> R.layout.item_bank_account_rp
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemCouponVM(list[position].any as Coupon, itemClick, selectedCoupon)
    }

    fun swapData(items: List<Coupon>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_ITEM, it) })
        notifyDataSetChanged()
    }
}
