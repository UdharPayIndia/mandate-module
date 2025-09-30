package com.rocketpay.mandate.feature.common.domain

import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.login.presentation.injection.LoginComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class CommonUseCase {

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var kycUseCase: KycUseCase

    init {
        LoginComponent.Initializer.init().inject(this)
    }

    companion object {
        private lateinit var commonUseCase: CommonUseCase
        fun getInstance(): CommonUseCase {
            if (!::commonUseCase.isInitialized) {
                commonUseCase = CommonUseCase()
            }
            return commonUseCase
        }
    }

    fun getAccountId(): String{
        return loginUseCase.getAccountId()
    }

    fun getMobileNumber(): String{
        return loginUseCase.getMobileNumber()
    }

    fun getName(): String{
        return loginUseCase.getName()
    }

    suspend fun getKycStatus(): KycStateEnum {
        return kycUseCase.getKycStatusNonLive()
    }

    fun getKycStatusLive(): Flow<KycStateEnum> {
        return kycUseCase.getKycStatus()
    }
}
