package com.rocketpay.mandate.feature.charge.presentation.injection

import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFragment
import dagger.Component

@Component(modules = [ChargeModule::class])
internal interface ChargeComponent {

    fun inject(chargeFragment: ChargeFragment)

    object Initializer {

        fun init(): ChargeComponent {
            val chargeModule = ChargeModule()
            return DaggerChargeComponent.builder()
                .chargeModule(chargeModule).build()
        }
    }
}
