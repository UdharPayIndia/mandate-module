package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel

internal sealed class PaymentTrackerType(val value: String, val index: Int) {
    object Outstanding: PaymentTrackerType("Outstanding", 0)
    object Upcoming: PaymentTrackerType("Upcoming", 1)
    object Collected: PaymentTrackerType("Collected", 2)

    companion object{
        fun getPaymentTrackerType(value: String?): PaymentTrackerType{
            return when(value){
                Outstanding.value -> Outstanding
                Upcoming.value -> Upcoming
                Collected.value -> Collected
                else -> Outstanding
            }
        }

        fun getPaymentTrackerTypeByIndex(index: Int): PaymentTrackerType{
            return when(index){
                Upcoming.index -> Upcoming
                Collected.index -> Collected
                else -> Outstanding
            }
        }
    }
}