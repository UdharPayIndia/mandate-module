package com.rocketpay.mandate.feature.common.domain

import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase
import com.rocketpay.mandate.feature.login.presentation.injection.LoginComponent
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

internal class CommonUseCase {

    @Inject
    lateinit var loginUseCase: LoginUseCase

    @Inject
    lateinit var kycUseCase: KycUseCase

    @Inject
    lateinit var mandateUseCase: MandateUseCase

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

    fun getMandateStatusLive(referenceId: String): Flow<MandateState?> {
        return mandateUseCase.getMandateByReferenceId(referenceId).transform {
            emit(it?.state)
        }
    }

    fun getMandateUrl(referenceId: String): Flow<String?> {
        return mandateUseCase.getMandateByReferenceId(referenceId).transform {
            emit(it?.mandateUrl)
        }
    }
}
