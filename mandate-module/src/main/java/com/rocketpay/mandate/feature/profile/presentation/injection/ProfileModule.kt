package com.rocketpay.mandate.feature.profile.presentation.injection

import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycModule
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.login.presentation.injection.LoginModule
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import dagger.Module
import dagger.Provides

@Module(includes = [PropertyModule::class, KycModule::class, LoginModule::class])
internal open class ProfileModule {

    @Provides
    internal fun provideProfileVMFactory(
        propertyUseCase: PropertyUseCase,
        kycUseCase: KycUseCase,
        loginUseCase: LoginUseCase
    ): ProfileVMFactory {
        return ProfileVMFactory(kycUseCase, propertyUseCase, loginUseCase)
    }


}
