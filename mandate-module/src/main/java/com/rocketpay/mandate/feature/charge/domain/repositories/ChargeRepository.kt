package com.rocketpay.mandate.feature.charge.domain.repositories

import com.rocketpay.mandate.feature.charge.domain.entities.Charge

internal interface ChargeRepository {
    fun getCharge(): Charge
}
