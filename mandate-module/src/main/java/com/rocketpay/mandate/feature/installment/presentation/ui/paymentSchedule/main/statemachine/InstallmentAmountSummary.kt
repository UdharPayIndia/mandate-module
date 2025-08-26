package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine

internal class InstallmentAmountSummary(
    val outstandingCount: Int = 0,
    val outstandingAmount: Double = 0.0,
    val upcomingCount: Int = 0,
    val upcomingAmount: Double = 0.0,
    val collectedCount: Int = 0,
    val collectedAmount: Double = 0.0
)