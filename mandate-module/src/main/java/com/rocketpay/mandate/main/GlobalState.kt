package com.rocketpay.mandate.main

import kotlinx.coroutines.flow.MutableStateFlow

internal object GlobalState {
    var isRefreshing = MutableStateFlow(false)
    var isSettlementRefreshing = MutableStateFlow(false)
    val isLogin = MutableStateFlow(false)

}