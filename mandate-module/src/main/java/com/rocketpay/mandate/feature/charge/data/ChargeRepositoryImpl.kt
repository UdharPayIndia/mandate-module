package com.rocketpay.mandate.feature.charge.data

import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.feature.charge.domain.entities.ChargeType
import com.rocketpay.mandate.feature.charge.domain.repositories.ChargeRepository

internal class ChargeRepositoryImpl(
): ChargeRepository {

    override fun getCharge(): Charge {
        return Charge(
            1.0,
            0.0,
            ChargeType.Percentage
        )
    }
}
