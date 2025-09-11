package com.rocketpay.mandate.feature.webview.presentation.injection

import com.rocketpay.mandate.feature.webview.presentation.ui.view.GenericWebviewFragment
import dagger.Component

@Component(modules = [GenericWebviewModule::class])
internal interface GenericWebviewComponent {

    fun inject(genericWebviewFragment: GenericWebviewFragment)

    object Initializer {

        fun init(): GenericWebviewComponent {
            val genericWebviewModule = GenericWebviewModule()
            return DaggerGenericWebviewComponent.builder()
                .genericWebviewModule(genericWebviewModule).build()
        }
    }
}
