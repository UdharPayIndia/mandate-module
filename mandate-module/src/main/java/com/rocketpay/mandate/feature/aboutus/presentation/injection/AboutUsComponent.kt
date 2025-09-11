package com.rocketpay.mandate.feature.aboutus.presentation.injection

import com.rocketpay.mandate.feature.aboutus.presentation.ui.view.AboutUsFragment
import dagger.Component

@Component(modules = [AboutUsModule::class])
internal interface AboutUsComponent {

    fun inject(aboutUsFragment: AboutUsFragment)

    object Initializer {

        fun init(): AboutUsComponent {
            val aboutUsModule = AboutUsModule()
            return DaggerAboutUsComponent.builder()
                .aboutUsModule(aboutUsModule)
                .build()
        }
    }
}
