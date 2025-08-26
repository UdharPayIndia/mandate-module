package com.rocketpay.mandate.feature.login.presentation.injection


import com.rocketpay.mandate.feature.login.data.AppSignatureRepositoryImpl
import com.rocketpay.mandate.feature.login.data.LoginRepositoryImpl
import com.rocketpay.mandate.feature.login.data.datasource.local.LoginDataStore
import com.rocketpay.mandate.feature.login.data.datasource.remote.LoginService
import com.rocketpay.mandate.feature.login.data.mapper.UserMapper
import com.rocketpay.mandate.feature.login.domain.repositories.AppSignatureRepository
import com.rocketpay.mandate.feature.login.domain.repositories.LoginRepository
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycModule
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import dagger.Module
import dagger.Provides


@Module(includes = [KycModule::class, PropertyModule::class])
internal open class LoginModule {

    @Provides
    internal fun provideLoginStateMachineFactory(
        loginUseCase: LoginUseCase,
        kycUseCase: KycUseCase,
        propertyUseCase: PropertyUseCase): LoginStateMachineFactory {
        return LoginStateMachineFactory(loginUseCase, kycUseCase, propertyUseCase)
    }

    @Provides
    internal fun provideLoginUseCase(
        loginRepository: LoginRepository,
        appSignatureRepository: AppSignatureRepository
    ): LoginUseCase {
        return LoginUseCase(loginRepository, DataValidator(), appSignatureRepository
        )
    }

    @Provides
    internal fun provideLoginRepository(
        loginService: LoginService,
        loginDataStore: LoginDataStore): LoginRepository {
        return LoginRepositoryImpl(loginDataStore, loginService, UserMapper()
        )
    }

    @Provides
    internal fun provideAppSignatureRepository(): AppSignatureRepository {
        return AppSignatureRepositoryImpl(MandateManager.getInstance().getContext())
    }

    @Provides
    internal fun provideLoginService(): LoginService {
        return LoginService()
    }

    @Provides
    internal fun provideLoginDataStore(): LoginDataStore {
        return LoginDataStore(DataStore(MandateManager.getInstance().getContext(), LoginDataStore.LOGIN_DATA_STORE))
    }

}
