package com.rocketpay.mandate.main.presentation.injection

import com.rocketpay.mandate.main.presentation.view.RpMainActivity
import dagger.Component

@Component(modules = [RpMainModule::class])
internal interface RpMainComponent {

    fun inject(rpMainActivity: RpMainActivity)


    object Initializer {

        fun init(): RpMainComponent {
            return DaggerRpMainComponent.builder().build()
        }
    }
}
