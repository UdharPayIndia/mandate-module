package com.rocketpay.mandate.feature.mandate.domain.entities

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class PaymentMode(val value: String, val translation: String) {
    object Online : PaymentMode("online", ResourceManager.getInstance().getString(R.string.rp_online))
    object Offline : PaymentMode("offline", ResourceManager.getInstance().getString(R.string.rp_cash))

    companion object {
        val map by lazy {
            mapOf(
                "online" to Online,
                "offline" to Offline,
            )
        }

        fun get(mode: String?): PaymentMode? {
            return map[mode]
        }
    }
}