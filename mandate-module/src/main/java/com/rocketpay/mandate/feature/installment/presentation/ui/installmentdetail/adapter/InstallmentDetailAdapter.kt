package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.adapter

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentJourney
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel.ItemInstallmentDetailVM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter

internal class InstallmentDetailAdapter : RecyclerViewAdapter() {

    lateinit var itemClick: (InstallmentDetailEvent) -> Unit
    private var isExpanded: Boolean = false
    private var totalCount: Int = 0
    private var isManualMandate: Boolean = false
    private var dueDate: Long = 0
    private var isMerchantCollected: Boolean = false

    companion object {
        const val VIEW_TYPE_INSTALLMENT_DETAIL = "VIEW_TYPE_INSTALLMENT_DETAIL"
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_installment_detail_rp
    }

    override fun getViewModel(position: Int): RecyclerViewItemViewModel {
        return ItemInstallmentDetailVM(
            list[position].any as InstallmentJourney,
            position,
            isExpanded,
            itemClick,
            list.count(),
            totalCount,
            isManualMandate,
            dueDate,
            isMerchantCollected
        )
    }

    fun swapData(items: List<InstallmentJourney>, isExpanded: Boolean,
                 totalCount: Int, isManualMandate: Boolean, dueDate: Long,
                 isMerchantCollected: Boolean) {
        this.isExpanded = isExpanded
        this.totalCount = totalCount
        this.isManualMandate = isManualMandate
        this.dueDate = dueDate
        this.isMerchantCollected = isMerchantCollected
        list.clear()
        list.addAll(items.map { RecyclerViewItem(VIEW_TYPE_INSTALLMENT_DETAIL, it) })
        notifyDataSetChanged()
    }
}
