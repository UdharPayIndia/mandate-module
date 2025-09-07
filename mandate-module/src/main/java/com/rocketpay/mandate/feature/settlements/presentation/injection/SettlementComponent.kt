package com.rocketpay.mandate.feature.settlements.presentation.injection

import com.rocketpay.mandate.feature.settlements.data.PaymentOrderReportSyncer
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderSyncer
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.view.SettlementDetailFragment
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.view.SettlementListFragment
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.view.SettlementMainFragment
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.view.DownloadReportFragment
import dagger.Component

@Component(modules = [SettlementModule::class])
internal interface SettlementComponent {

    fun inject(paymentOrderSyncer: PaymentOrderSyncer)
    fun inject(settlementMainFragment: SettlementMainFragment)
    fun inject(settlementListFragment: SettlementListFragment)
    fun inject(settlementDetailFragment: SettlementDetailFragment)
    fun inject(downloadReportFragment: DownloadReportFragment)
    fun inject(paymentOrderReportSyncer: PaymentOrderReportSyncer)

    object Initializer {

        fun init(): SettlementComponent {
            return DaggerSettlementComponent.builder().build()
        }
    }
}
