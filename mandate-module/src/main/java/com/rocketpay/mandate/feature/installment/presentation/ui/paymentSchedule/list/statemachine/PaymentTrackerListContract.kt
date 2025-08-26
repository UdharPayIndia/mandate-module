package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine

import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithMandateEntity
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class PaymentTrackerListState(
    val paymentTrackerType: PaymentTrackerType = PaymentTrackerType.Outstanding,
    val installments: ArrayList<InstallmentWithMandateEntity> = arrayListOf(),
    val isLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val lastFetchedTimeStamp: Long = 0L,
    val limit: Int = 20,
    val orderByDesc: Boolean = false,
    val isSuperKeyFlow: Boolean = false,
    val skipManualMandate: Boolean = false
) : BaseState(PaymentTrackerListScreen)

internal sealed class PaymentTrackerListEvent(name: String? = null) : BaseEvent(name) {
    data class LoadInstallments(
        val paymentTrackerType: String?,
        val orderByDesc: Boolean,
        val isSuperKeyFlow: Boolean,
        val skipManualMandate: Boolean
    ): PaymentTrackerListEvent()
    data class InstallmentsLoaded(
        val installments: ArrayList<InstallmentWithMandateEntity>
    ) : PaymentTrackerListEvent()
    data class InstallmentClick(
        val installment: InstallmentWithMandateEntity
    ) : PaymentTrackerListEvent("sk_payment_instalment_click")
    object FetchNextInstallments: PaymentTrackerListEvent()
    object RefreshInstallments: PaymentTrackerListEvent()
}

internal sealed class PaymentTrackerListASF : AsyncSideEffect {
    data class LoadInstallments(
        val paymentTrackerType: PaymentTrackerType,
        val lastFetchedTimeStamp: Long,
        val limit: Int,
        val orderByDesc: Boolean,
        val isSuperKeyFlow: Boolean,
        val skipManualMandate: Boolean
    ) : PaymentTrackerListASF()
}

internal sealed class PaymentTrackerListUSF : UiSideEffect {
    data class UpdateInstallments(
        val installments: List<InstallmentWithMandateEntity>,
        val isLastPage: Boolean,
        val hideTag: Boolean): PaymentTrackerListUSF()
    data class ShowToast(val message: String) : PaymentTrackerListUSF()
    data class OpenMandateDetails(val mandateId: String, val installmentSerialNumber: Int): PaymentTrackerListUSF()
}

internal object PaymentTrackerListScreen : Screen("payment_tracker")
