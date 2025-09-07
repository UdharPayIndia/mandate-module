package com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class DownloadReportState(
    val periodRange: PeriodRangeEnum? = null,
    val fromDate: Long? = null,
    val toDate: Long? = null
) : BaseState(DownloadReportScreen)

internal sealed class DownloadReportEvent(name: String? = null) : BaseEvent(name) {
    data object DownloadReportClick: DownloadReportEvent()
    data class PeriodRangeClicked(val periodRangeEnum: PeriodRangeEnum): DownloadReportEvent()
    data class FromDateClicked(val periodRangeEnum: PeriodRangeEnum): DownloadReportEvent()
    data class ToDateClicked(val periodRangeEnum: PeriodRangeEnum): DownloadReportEvent()
    data class FromDateSelected(val fromDate: Long?): DownloadReportEvent()
    data class ToDateSelected(val fromDate: Long?): DownloadReportEvent()

}


internal sealed class DownloadReportASF : AsyncSideEffect {
    data class DownloadReport(
        val periodRange: PeriodRangeEnum,
        val fromDate: Long?,
        val toDate: Long?
    ): DownloadReportASF()
}


internal sealed class DownloadReportUSF : UiSideEffect {
    data class ShowMessage(
        val message: String
    ): DownloadReportUSF()
    data object CloseScreen: DownloadReportUSF()
    data object OpenFromDateCalender: DownloadReportUSF()
    data class OpenToDateCalender(val fromDate: Long): DownloadReportUSF()
}

internal object DownloadReportScreen : Screen("download_report")
