package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.ItemMandateDetailVM
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.ItemMandateHeaderVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class MandateDetailAdapter : RecyclerViewAdapter() {

    var showMandateTag: Boolean = false
    var nextInstallmentId: String? = null
    var manualMandate: Boolean = false
    var mandate: Mandate? = null
    lateinit var itemClick: (MandateDetailEvent) -> Unit

    companion object {
        const val VIEW_ITEM_HEADER = "VIEW_ITEM_HEADER"
        const val VIEW_TYPE_MANDATE_DETAIL = "VIEW_TYPE_MANDATE_DETAIL"
    }

    override fun getItemViewType(position: Int): Int {
        return when(list[position].viewType){
            VIEW_ITEM_HEADER -> {
                R.layout.item_mandate_header_rp
            }
            else -> {
                R.layout.item_mandate_detail_rp
            }
        }
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return when(list[position].viewType){
            VIEW_ITEM_HEADER -> {
                ItemMandateHeaderVM(mandate)
            }
            else -> {
                ItemMandateDetailVM(
                    list[position].any as Installment, nextInstallmentId,
                    itemClick, showMandateTag, manualMandate
                )
            }
        }
    }

    fun swapData(
        items: List<Installment>,
        nextInstallmentId: String?,
        showMandateTag: Boolean,
        manualMandate: Boolean,
        mandate: Mandate?
    ) {
        this.mandate = mandate
        this.nextInstallmentId = nextInstallmentId
        this.showMandateTag = showMandateTag
        this.manualMandate = manualMandate
        list.clear()
        list.add(RecyclerViewItem(VIEW_ITEM_HEADER, mandate))
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_MANDATE_DETAIL, it) })
        notifyDataSetChanged()
    }
}
