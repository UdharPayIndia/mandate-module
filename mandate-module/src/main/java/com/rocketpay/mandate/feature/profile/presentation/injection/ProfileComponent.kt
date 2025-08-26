package com.rocketpay.mandate.feature.profile.presentation.injection

import com.rocketpay.mandate.feature.profile.presentation.ui.view.UserProfileFragment
import dagger.Component


@Component(modules = [ProfileModule::class])
internal interface ProfileComponent {

    fun inject(userProfileFragment: UserProfileFragment)


    object Initializer {

        fun init(): ProfileComponent {
            return DaggerProfileComponent.builder().build()
        }
    }
}
