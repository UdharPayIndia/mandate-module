package com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine

import android.os.CountDownTimer

internal class OtpTimer(
    otpTimeout: Long,
    interval: Long,
    val onTimeUpdated: (Long) -> Unit,
    val onTimeout: () -> Unit
): CountDownTimer(otpTimeout, interval) {

    override fun onTick(timeLeft: Long) {
        onTimeUpdated(timeLeft)
    }

    override fun onFinish() {
        onTimeout()
    }
}
