package com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.data.network.NetworkUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.settlements.data.PaymentOrderReportSyncer
import com.rocketpay.mandate.main.GlobalState
import kotlinx.coroutines.CoroutineScope

internal class DownloadReportStateMachine (
): SimpleStateMachineImpl<DownloadReportEvent, DownloadReportState, DownloadReportASF, DownloadReportUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): DownloadReportState {
        return DownloadReportState()
    }

    override fun handleEvent(
        event: DownloadReportEvent,
        state: DownloadReportState
    ): Next<DownloadReportState?, DownloadReportASF?, DownloadReportUSF?> {
        return when (event) {
            is DownloadReportEvent.DownloadReportClick -> {
                if(GlobalState.isReportStatus.value.first == ProgressDialogStatus.Progress){
                    next(
                        DownloadReportUSF.ShowMessage(
                        ResourceManager.getInstance().getString(R.string.rp_download_report_is_already_in_progress))
                    )
                }else{
                    if((state.periodRange != null && state.periodRange != PeriodRangeEnum.Custom)
                        || (state.periodRange == PeriodRangeEnum.Custom && state.fromDate != null && state.toDate != null)){
                        if(NetworkUtils.isNetworkAvailable()) {
                            next(
                                DownloadReportASF.DownloadReport(
                                    state.periodRange,
                                    state.fromDate,
                                    state.toDate
                                ),
                                DownloadReportUSF.CloseScreen
                            )
                        }else{
                            next(DownloadReportUSF.ShowMessage(ResourceManager.getInstance().getString(R.string.rp_no_internet)))
                        }
                    }else{
                        next(DownloadReportUSF.ShowMessage(ResourceManager.getInstance().getString(R.string.rp_please_select_the_date_range)))
                    }
                }
            }
            is DownloadReportEvent.PeriodRangeClicked -> {
                next(state.copy(periodRange = event.periodRangeEnum, fromDate = null, toDate = null))
            }
            is DownloadReportEvent.FromDateClicked -> {
                next(state.copy(periodRange = event.periodRangeEnum), DownloadReportUSF.OpenFromDateCalender)
            }
            is DownloadReportEvent.ToDateClicked -> {
                if (state.fromDate != null) {
                    next(
                        state.copy(periodRange = event.periodRangeEnum),
                        DownloadReportUSF.OpenToDateCalender(state.fromDate)
                    )
                }else{
                    next(DownloadReportUSF.ShowMessage(ResourceManager.getInstance().getString(R.string.rp_please_select_start_date)))
                }
            }
            is DownloadReportEvent.FromDateSelected -> {
                next(state.copy(fromDate = event.fromDate))
            }
            is DownloadReportEvent.ToDateSelected -> {
                next(state.copy(toDate = event.fromDate))

            }
        }
    }

    override fun dispatchEvent(event: DownloadReportEvent) {
        super.dispatchEvent(event)
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: DownloadReportASF,
        dispatchEvent: (DownloadReportEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when(sideEffect){
            is DownloadReportASF.DownloadReport -> {
                if(sideEffect.periodRange != PeriodRangeEnum.Custom) {
                    val periodTimeStamp = PeriodRangeEnum.Companion.getTimeStamp(sideEffect.periodRange)
                    GlobalState.fromTimeStamp.value = periodTimeStamp.first
                    GlobalState.toTimeStamp.value = periodTimeStamp.second
                }else{
                    GlobalState.fromTimeStamp.value = sideEffect.fromDate.long()
                    GlobalState.toTimeStamp.value = sideEffect.toDate.long()
                }
                SyncManager.getInstance().enqueue(PaymentOrderReportSyncer.TYPE)
            }
        }
    }

}

