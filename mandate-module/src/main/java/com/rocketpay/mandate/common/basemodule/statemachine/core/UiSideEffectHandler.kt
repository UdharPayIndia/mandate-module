package com.rocketpay.mandate.common.basemodule.statemachine.core

import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal interface UiSideEffectHandler<USF: UiSideEffect> {
    fun registerUiSideEffectObserver()
    fun handleUiSideEffect(sideEffect: USF)
}
