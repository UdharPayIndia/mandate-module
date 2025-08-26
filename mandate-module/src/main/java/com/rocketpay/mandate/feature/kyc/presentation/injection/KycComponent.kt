package com.rocketpay.mandate.feature.kyc.presentation.injection

import com.rocketpay.mandate.feature.kyc.data.KycSyncer
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.view.KycIdentityIntroBottomSheetFragment
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view.KycFragment
import dagger.Component

@Component(modules = [KycModule::class])
internal interface KycComponent {

    fun inject(kycFragment: KycFragment)
    fun inject(kycIdentityIntroBottomSheetFragment: KycIdentityIntroBottomSheetFragment)
    fun inject(kycSyncer: KycSyncer)
    object Initializer {

        fun init(): KycComponent {
            val kycModule = KycModule()
            return DaggerKycComponent.builder()
                .kycModule(kycModule)
                .build()
        }
    }
}
