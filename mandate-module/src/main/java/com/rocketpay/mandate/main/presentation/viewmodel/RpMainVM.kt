package com.rocketpay.mandate.main.presentation.viewmodel

import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.login.domain.usecase.LoginUseCase

internal class RpMainVM(
    private val loginUseCase: LoginUseCase,
    private val kycUseCase: KycUseCase
): BaseMainUM() {

    var flowType: String = ""
    var referenceId: String = ""
    var customerName: String = ""
    var customerNumber: String = ""
    var amount: Long = 0
    var note: String = ""

    fun isLoggedIn(): Boolean{
        return loginUseCase.isLoggedIn()
    }

    fun isKycCompleted(): Boolean{
        return kycUseCase.isKycCompleted()
    }
}