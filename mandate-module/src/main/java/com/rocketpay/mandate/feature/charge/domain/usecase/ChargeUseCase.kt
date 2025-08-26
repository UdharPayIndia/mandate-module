package com.rocketpay.mandate.feature.charge.domain.usecase

import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.feature.charge.domain.repositories.ChargeRepository

internal class ChargeUseCase internal constructor(private val chargeRepository: ChargeRepository) {

    fun getCharge(): Charge {
        return chargeRepository.getCharge()
    }
}
