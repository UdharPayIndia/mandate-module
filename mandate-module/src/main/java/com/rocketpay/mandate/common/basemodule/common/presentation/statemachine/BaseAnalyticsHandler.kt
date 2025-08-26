package com.rocketpay.mandate.common.basemodule.common.presentation.statemachine

import androidx.annotation.CallSuper
import com.rocketpay.mandate.common.mvistatemachine.contract.AnalyticsEventHandler

internal open class BaseAnalyticsHandler<E : BaseEvent, S : BaseState> : AnalyticsEventHandler<E, S> {

    companion object {
        const val EVENT_PARAM_SCREEN_NAME = "screen_name"
    }

    @CallSuper
    open fun updateCommonEventParameter(state: S, paramBuilder: HashMap<String, Any>) {
        paramBuilder[EVENT_PARAM_SCREEN_NAME] = state.screen.name
    }

    @CallSuper
    open fun updateEventParameter(event: E, state: S, paramBuilder: HashMap<String, Any>) {
    }

    override fun sendEvent(event: E, state: S) {
        event.name?.let {
            val paramBuilder = hashMapOf<String, Any>()
            updateCommonEventParameter(state, paramBuilder)
            updateEventParameter(event, state, paramBuilder)
            event.name?.let {
            }
        }
    }
}
