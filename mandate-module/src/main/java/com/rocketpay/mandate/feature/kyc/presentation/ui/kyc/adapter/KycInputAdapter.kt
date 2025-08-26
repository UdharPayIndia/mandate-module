package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.viewmodel.ItemTextInputVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class KycInputAdapter: RecyclerViewAdapter() {

    lateinit var itemClick: (KycEvent) -> Unit
    lateinit var onInputVerified: () -> Unit

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_text_input_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemTextInputVM(list[position].any as KycItemInputMeta, itemClick, onInputVerified)
    }

    fun swapData(items: List<KycItemInputMeta>) {
        list.clear()
        list.addAll(items.map { RecyclerViewItem(it.type, it) })
        notifyDataSetChanged()
    }
}
