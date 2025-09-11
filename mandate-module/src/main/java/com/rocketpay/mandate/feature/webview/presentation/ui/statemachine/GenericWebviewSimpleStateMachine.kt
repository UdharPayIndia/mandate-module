package com.rocketpay.mandate.feature.webview.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import kotlinx.coroutines.CoroutineScope

internal class GenericWebviewSimpleStateMachine: SimpleStateMachineImpl<GenericWebviewEvent, GenericWebviewState, GenericWebviewASF, GenericWebviewUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): GenericWebviewState {
        return GenericWebviewState()
    }

    override fun handleEvent(
        event: GenericWebviewEvent,
        state: GenericWebviewState
    ): Next<GenericWebviewState?, GenericWebviewASF?, GenericWebviewUSF?> {
        return when (event) {
            is GenericWebviewEvent.LoadPage -> {
                val finalUrl = event.url ?: state.url
                if (finalUrl.isNullOrEmpty()) {
                    noChange()
                } else {
                    next(
                        state.copy(url = finalUrl),
                        GenericWebviewUSF.LoadUrl(finalUrl)
                    )
                }
            }
            is GenericWebviewEvent.LoadUi -> {
                next(
                    state.copy(
                        toolbarTitle = event.toolbarTitle ?: state.toolbarTitle,
                        shouldEnableCache = event.shouldEnableCache ?: state.shouldEnableCache
                    ),
                    GenericWebviewUSF.LoadUi(state.shouldEnableCache)
                )
            }
            is GenericWebviewEvent.HandleAction -> {
                if (isValidSecret(event.secret)) {
                    next(GenericWebviewUSF.HandleSpecificAction(event.action, event.data))
                } else {
                    noChange()
                }
            }
        }
    }

    private fun isValidSecret(secret: String?): Boolean {
        return true
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: GenericWebviewASF,
        dispatchEvent: (GenericWebviewEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            GenericWebviewASF.GenericWebviewASF1 -> {
            }
        }
    }
}
