package com.rocketpay.mandate.feature.settlements.presentation.ui.report.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.DownloadReportState
import com.rocketpay.mandate.feature.settlements.presentation.ui.report.statemachine.PeriodRangeEnum

internal class DownloadReportUM (private val dispatchEvent: (DownloadReportEvent) -> Unit) : BaseMainUM() {

    val selectedPeriodRange = ObservableField<PeriodRangeEnum>()
    val fromDate = ObservableField<String>()
    val toDate = ObservableField<String>()


    fun handleState(state: DownloadReportState) {
        if(state.fromDate != null) {
            fromDate.set(
                DateUtils.getDate(
                    state.fromDate,
                    DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR
                )
            )
        }else{
            fromDate.set(null)
        }
        if(state.toDate != null) {
            toDate.set(
                DateUtils.getDate(
                    state.toDate,
                    DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR
                )
            )
        }else{
            toDate.set(null)
        }
        selectedPeriodRange.set(state.periodRange)
    }

    fun onDownloadClick(){
        dispatchEvent(DownloadReportEvent.DownloadReportClick)
    }

}
