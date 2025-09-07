package com.rocketpay.mandate.main

import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialogStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal object GlobalState {
    var isRefreshing = MutableStateFlow(false)
    var isSettlementRefreshing = MutableStateFlow(false)
    val isLogin = MutableStateFlow(false)
    var isReportStatus = MutableStateFlow<Pair<ProgressDialogStatus, String?>>(ProgressDialogStatus.Init to null)
    var fromTimeStamp = MutableStateFlow(0L)
    var toTimeStamp = MutableStateFlow(0L)
}