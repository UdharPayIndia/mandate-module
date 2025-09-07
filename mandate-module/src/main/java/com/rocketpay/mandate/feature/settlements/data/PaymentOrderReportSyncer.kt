package com.rocketpay.mandate.feature.settlements.data

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.download.RocketpayDownloadManager
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.getFileNameFromUrl
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.image.presentation.utils.FileUtils
import com.rocketpay.mandate.feature.settlements.domain.repositories.PaymentOrderRepository
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.main.GlobalState
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.syncmanager.domain.enities.DefaultSyncConstraint
import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncFailurePolicy
import com.udharpay.core.syncmanager.domain.enities.SyncPriority
import com.udharpay.core.syncmanager.domain.enities.SyncStatus
import com.udharpay.core.syncmanager.domain.repositories.Sync
import com.udharpay.core.syncmanager.domain.repositories.Syncer
import java.io.File
import javax.inject.Inject

internal class PaymentOrderReportSync: Sync {
    override fun dependencies() = emptyList<String>()
    override fun priority() = SyncPriority.Medium
    override fun existingSyncPolicy() = ExistingSyncPolicy.Keep
    override fun syncFailurePolicy() = SyncFailurePolicy.Cascade
    override fun syncer() = PaymentOrderReportSyncer()
    override fun constraint() = DefaultSyncConstraint.getLoginAndNetworkConstraint(
        isLoginRequire = true,
        isNetworkRequire = true
    )
}

internal class PaymentOrderReportSyncer: Syncer {

    @Inject
    lateinit var paymentOrderRepository: PaymentOrderRepository

    companion object {
        const val TYPE = "payment-order-report"
    }

    init {
        SettlementComponent.Initializer.init().inject(this)
    }

    override suspend fun sync(): SyncStatus {
        GlobalState.isReportStatus.value = ProgressDialogStatus.Progress to ResourceManager.getInstance().getString(
            R.string.rp_download_report_has_been_initiated)
        when(val outcome = paymentOrderRepository.fetchPaymentOrderReport(
            GlobalState.fromTimeStamp.value,
            GlobalState.toTimeStamp.value
        )) {
            is Outcome.Error -> {
                GlobalState.isReportStatus.value = ProgressDialogStatus.Error to outcome.error.message
                return SyncStatus.Failed
            }
            is Outcome.Success -> {
                val fileName = outcome.data.url.getFileNameFromUrl() ?: System.currentTimeMillis().toString()
                val destFolder = FileUtils.getPublicDocsFolder()
                val destinationFile = File(destFolder, fileName)
                val uri = RocketpayDownloadManager.download(outcome.data.url,
                    destinationFile, fileName, true)
                GlobalState.isReportStatus.value = ProgressDialogStatus.Success to uri.toString()
                return SyncStatus.Success
            }
        }
    }
}
