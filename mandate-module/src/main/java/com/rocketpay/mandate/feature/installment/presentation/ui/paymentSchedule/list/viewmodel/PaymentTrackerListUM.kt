package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListState
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class PaymentTrackerListUM (
    private val dispatchEvent: (PaymentTrackerListEvent) -> Unit
) : BaseMainUM() {

    val emptyStateVisibility = ObservableBoolean()
    val titleText = ObservableField<String>()
    val subText = ObservableField<String>()

    var paymentTrackerType : PaymentTrackerType? = null
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    var installmentsSize: Int = 0

    fun handleState(state: PaymentTrackerListState) {
        paymentTrackerType = state.paymentTrackerType
        installmentsSize = state.installments.size
        isLastPage = state.isLastPage
        isLoading = state.isLoading
        if (state.installments.isEmpty()) {
            emptyStateVisibility.set(true)
            when(state.paymentTrackerType) {
                PaymentTrackerType.Outstanding -> {
                    titleText.set(
                        ResourceManager.getInstance()
                            .getString(R.string.rp_no_outstanding_installments)
                    )
                    if (state.isSuperKeyFlow) {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_all_outstanding_installment_will_be_here)
                        )
                    } else {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_mandate_all_outstanding_installment_will_be_here)
                        )
                    }
                }

                PaymentTrackerType.Upcoming -> {
                    titleText.set(
                        ResourceManager.getInstance().getString(R.string.rp_no_upcoming_installments)
                    )
                    if (state.isSuperKeyFlow) {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_all_upcoming_installment_will_be_here)
                        )
                    } else {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_mandate_all_upcoming_installment_will_be_here)
                        )
                    }
                }

                else -> {
                    titleText.set(
                        ResourceManager.getInstance().getString(R.string.rp_no_collected_installments)
                    )
                    if (state.isSuperKeyFlow) {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_all_collected_installment_will_be_here)
                        )
                    } else {
                        subText.set(
                            ResourceManager.getInstance()
                                .getString(R.string.rp_mandate_all_collected_installment_will_be_here)
                        )
                    }
                }
            }
        }else{
            emptyStateVisibility.set(false)
        }
    }
}