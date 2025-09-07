package com.rocketpay.mandate.feature.mandate.domain.entities

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class PaymentMethod(val value: String, val transalion: String) {
    object Upi : PaymentMethod("upi", ResourceManager.getInstance().getString(R.string.rp_upi))
    object Nach : PaymentMethod("emandate", ResourceManager.getInstance().getString(R.string.rp_nach))
    object Manual : PaymentMethod("manual", ResourceManager.getInstance().getString(R.string.rp_manual))

    companion object {
        val map by lazy {
            mapOf(
                "upi" to Upi,
                "emandate" to Nach,
                "manual" to Manual
            )
        }

        fun get(mode: String?): PaymentMethod {
            return map[mode] ?: Upi
        }
    }
}
