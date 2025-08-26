package com.rocketpay.mandate.feature.mandate.presentation.injection

import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.view.InstallmentAddFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.view.InstallmentDetailFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.view.InstallmentUpdateFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.view.PaymentTrackerListFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.view.PaymentTrackerMainFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.view.EnterPenaltyAmountFragment
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.view.SelectRetryPeriodFragment
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view.MandateAddFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.view.MandateListFragment
import dagger.Component

@Component(modules = [MandateModule::class])
internal interface MandateComponent {

    fun inject(mandateListFragment: MandateListFragment)
    fun inject(mandateDetailFragment: MandateDetailFragment)
    fun inject(mandateSyncer: MandateSyncer)

    fun inject(installmentDetailFragment: InstallmentDetailFragment)
    fun inject(installmentAddFragment: InstallmentAddFragment)
    fun inject(installmentUpdateFragment: InstallmentUpdateFragment)
    fun inject(paymentTrackerListFragment: PaymentTrackerListFragment)
    fun inject(paymentTrackerMainFragment: PaymentTrackerMainFragment)

    fun inject(mandateAddFragment: MandateAddFragment)
    fun inject(enterPenaltyAmountFragment: EnterPenaltyAmountFragment)
    fun inject(selectRetryPeriodFragment: SelectRetryPeriodFragment)

    object Initializer {

        fun init(): MandateComponent {
            return DaggerMandateComponent.builder()
                .build()
        }
    }
}
