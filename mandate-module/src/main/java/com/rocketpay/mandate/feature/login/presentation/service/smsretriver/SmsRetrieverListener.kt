package com.rocketpay.mandate.feature.login.presentation.service.smsretriver

internal interface SmsRetrieverListener {
    fun onSmsReceived(otp: String?)
}
