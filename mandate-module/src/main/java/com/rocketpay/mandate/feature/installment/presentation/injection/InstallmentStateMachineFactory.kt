package com.rocketpay.mandate.feature.installment.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine.InstallmentDetailStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodStateMachine
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator

@Suppress("UNCHECKED_CAST")
internal open class InstallmentStateMachineFactory(
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase,
    private val propertyUseCase: PropertyUseCase,
    private val paymentOrderUseCase: PaymentOrderUseCase,
    private val dataValidator: DataValidator
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InstallmentDetailStateMachine::class.java) -> InstallmentDetailStateMachine(mandateUseCase, installmentUseCase, propertyUseCase, paymentOrderUseCase) as T
            modelClass.isAssignableFrom(InstallmentAddStateMachine::class.java) -> InstallmentAddStateMachine(mandateUseCase, installmentUseCase, dataValidator) as T
            modelClass.isAssignableFrom(InstallmentUpdateStateMachine::class.java) -> InstallmentUpdateStateMachine(installmentUseCase) as T
            modelClass.isAssignableFrom(EnterPenaltyAmountStateMachine::class.java) -> EnterPenaltyAmountStateMachine(installmentUseCase) as T
            modelClass.isAssignableFrom(SelectRetryPeriodStateMachine::class.java) -> SelectRetryPeriodStateMachine(installmentUseCase) as T
            modelClass.isAssignableFrom(PaymentTrackerListStateMachine::class.java) -> PaymentTrackerListStateMachine(installmentUseCase) as T
            modelClass.isAssignableFrom(PaymentTrackerMainStateMachine::class.java) -> PaymentTrackerMainStateMachine(installmentUseCase) as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
