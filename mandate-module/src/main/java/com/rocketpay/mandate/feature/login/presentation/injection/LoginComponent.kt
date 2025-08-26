package com.rocketpay.mandate.feature.login.presentation.injection

import com.rocketpay.mandate.feature.common.domain.CommonUseCase
import com.rocketpay.mandate.feature.login.presentation.ui.login.view.LoginFragment
import dagger.Component

@Component(modules = [LoginModule::class])
internal interface LoginComponent {

    fun inject(loginFragment: LoginFragment)
    fun inject(commonUseCase: CommonUseCase)

    object Initializer {

        fun init(): LoginComponent {
            return DaggerLoginComponent.builder()
                .build()
        }
    }
}
