package com.rocketpay.mandate.feature.business.presentation.injection

import com.rocketpay.mandate.feature.business.data.BusinessPropertySyncer
import com.rocketpay.mandate.feature.business.presentation.ui.view.BusinessProfileFragment
import dagger.Component


@Component(modules = [BusinessProfileModule::class])
internal interface BusinessProfileComponent {

    fun inject(businessProfileFragment: BusinessProfileFragment)
    fun inject(businessProfileSyncer: BusinessPropertySyncer)

    object Initializer {

        fun init(): BusinessProfileComponent {
            return DaggerBusinessProfileComponent.builder()
                .build()
        }
    }
}
