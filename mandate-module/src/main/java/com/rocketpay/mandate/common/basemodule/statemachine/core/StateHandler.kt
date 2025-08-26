package com.rocketpay.mandate.common.basemodule.statemachine.core

import com.rocketpay.mandate.common.mvistatemachine.contract.State

internal interface StateHandler<S: State> {
    fun registerStateObserver()
    fun handleState(state: S)
}
