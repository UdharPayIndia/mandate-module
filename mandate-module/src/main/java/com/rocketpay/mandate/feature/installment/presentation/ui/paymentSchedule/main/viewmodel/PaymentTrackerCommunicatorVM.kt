package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class PaymentTrackerCommunicatorVM: ViewModel() {
    val refreshCollectedInstallments = MutableLiveData<Boolean>()
    val refreshOutstandingInstallments = MutableLiveData<Boolean>()
    val refreshUpcomingInstallments = MutableLiveData<Boolean>()

}