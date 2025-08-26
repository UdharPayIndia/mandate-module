package com.rocketpay.mandate.feature.business.presentation.ui.adapter

import androidx.lifecycle.MutableLiveData
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.feature.business.domain.entities.BusinessField
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileEvent
import com.rocketpay.mandate.feature.business.presentation.ui.viewmodel.ItemBusinessProfileVM

internal class BusinessProfileAdapter : RecyclerViewAdapter() {

    lateinit var dispatchEvent: (BusinessProfileEvent) -> Unit
    var businessProperties = MutableLiveData<Map<String, String?>>()

    companion object {
        const val VIEW_TYPE_BUSINESS_FIELD = "VIEW_TYPE_BUSINESS_FIELD"
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].viewType) {
            VIEW_TYPE_BUSINESS_FIELD -> R.layout.item_business_profile_rp
            else -> R.layout.item_business_profile_rp
        }
    }

    override fun getViewModel(position: Int): ItemBusinessProfileVM {
        return ItemBusinessProfileVM(list[position].any as BusinessField, dispatchEvent, businessProperties)
    }

    fun swapData(items: List<BusinessField>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_BUSINESS_FIELD, it) })
        notifyDataSetChanged()
    }
}
