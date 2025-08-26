package com.rocketpay.mandate.common.mvistatemachine.contract

internal data class Next<S, ASF, USF>(val state: S, val asyncSideEffect: ASF, val uiSideEffect: USF)
