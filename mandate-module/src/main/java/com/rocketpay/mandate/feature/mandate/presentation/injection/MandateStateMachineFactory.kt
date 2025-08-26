package com.rocketpay.mandate.feature.mandate.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.rocketpay.mandate.feature.charge.domain.usecase.ChargeUseCase
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddStateMachine
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailStateMachine
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListStateMachine
import com.rocketpay.mandate.feature.permission.feature.domain.usecases.PermissionUseCase
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase

@Suppress("UNCHECKED_CAST")
internal open class MandateStateMachineFactory(
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase,
    private val bankAccountUseCase: BankAccountUseCase,
    private val permissionUseCase: PermissionUseCase,
    private val kycUseCase: KycUseCase,
    private val propertyUseCase: PropertyUseCase,
    private val chargeUseCase: ChargeUseCase,
    private val productUseCase: ProductUseCase
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MandateAddStateMachine::class.java) -> MandateAddStateMachine(
                mandateUseCase,
                bankAccountUseCase,
                permissionUseCase,
                chargeUseCase,
                productUseCase
            ) as T
            modelClass.isAssignableFrom(MandateDetailStateMachine::class.java) -> MandateDetailStateMachine(
                mandateUseCase,
                installmentUseCase,
                kycUseCase,
                propertyUseCase
            ) as T
            modelClass.isAssignableFrom(MandateListStateMachine::class.java) -> MandateListStateMachine(
                mandateUseCase,
            ) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
