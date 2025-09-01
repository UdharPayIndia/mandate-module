package com.rocketpay.mandate.main.presentation.injection

import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycModule
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.login.presentation.injection.LoginModule
import dagger.Module
import dagger.Provides


@Module(includes = [LoginModule::class, KycModule::class])
internal open class RpMainModule {

    @Provides
    internal fun provideMainActivityVMFactory(
        loginUseCase: LoginUseCase,
        kycUseCase: KycUseCase
    ): RpMainVMFactory {
        return RpMainVMFactory(loginUseCase, kycUseCase)
    }


}
