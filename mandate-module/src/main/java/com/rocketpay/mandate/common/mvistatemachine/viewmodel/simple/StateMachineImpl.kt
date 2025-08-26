package com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udharpay.kernel.kernelcommon.eventbus.EventBus
import com.udharpay.kernel.kernelcommon.eventbus.FlowEventBus
import com.rocketpay.mandate.common.mvistatemachine.liveevent.LiveEvent
import com.rocketpay.mandate.common.mvistatemachine.contract.*
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.StateMachine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class StateMachineImpl<E : Event, S : State, ASF : AsyncSideEffect, USF : UiSideEffect> private constructor(
    private var _state: S,
    private val eventHandler: EventHandler<E, S, ASF, USF>,
    private val asyncSideEffectHandler: AsyncSideEffectHandler<E, ASF>,
    private val analyticsEventHandler: AnalyticsEventHandler<E, S>?
) : StateMachine<E, S, USF>() {

    private val state: MutableLiveData<S> = MutableLiveData()
    private val uiSideEffect: LiveEvent<USF> = LiveEvent()
    private val eventBus: EventBus<E> = FlowEventBus(EVENT_BUFFERED)

    companion object {
        private const val EVENT_BUFFERED = 64
    }

    init {
        state.value = _state
        registerEventListener()
    }

    private fun registerEventListener() {
        viewModelScope.launch(Dispatchers.Default) {
            eventBus.listen { event ->
                val next = eventHandler.handleEvent(event, _state)
                handleNext(event, next)
            }
        }
    }

    override fun dispatchEvent(event: E) {
        viewModelScope.launch {
            eventBus.fire(event)
        }
    }

    override fun getState(): LiveData<S> {
        return state
    }

    override fun getUiSideEffect(): LiveData<USF> {
        return uiSideEffect
    }

    // /////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////

    private suspend fun handleNext(event: E, next: Next<S?, ASF?, USF?>) {
        dispatchAnalyticsEvent(event, _state)
        next.state?.let { _state = it }
        viewModelScope.launch {
            next.state?.let { dispatchState() }
            next.uiSideEffect?.let { dispatchUiSideEffect(it) }
        }
        next.asyncSideEffect?.let { dispatchAsyncSideEffect(it) }
    }

    private fun dispatchAnalyticsEvent(event: E, state: S) {
        analyticsEventHandler?.sendEvent(event, state)
    }

    private fun dispatchState() {
        state.value = _state
    }

    private fun dispatchUiSideEffect(sideEffect: USF) {
        uiSideEffect.value = sideEffect
    }

    private suspend fun dispatchAsyncSideEffect(sideEffect: ASF) {
        asyncSideEffectHandler.handleSideEffect(sideEffect, dispatchEvent, viewModelScope)
    }

    // /////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////

    private val dispatchEvent: (E) -> Unit = {
        dispatchEvent(it)
    }

    // /////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////

    class Builder<E : Event, S : State, ASF : AsyncSideEffect, USF : UiSideEffect> {

        private lateinit var eventHandler: EventHandler<E, S, ASF, USF>
        private lateinit var asyncSideEffectHandler: AsyncSideEffectHandler<E, ASF>
        private var analyticsEventHandler: AnalyticsEventHandler<E, S>? = null

        fun setEventHandler(eventHandler: EventHandler<E, S, ASF, USF>) = apply {
            this.eventHandler = eventHandler
        }

        fun setAsyncSideEffectHandler(asyncSideEffectHandler: AsyncSideEffectHandler<E, ASF>) = apply {
            this.asyncSideEffectHandler = asyncSideEffectHandler
        }

        fun setAnalyticsHandler(analyticsEventHandler: AnalyticsEventHandler<E, S>) = apply {
            this.analyticsEventHandler = analyticsEventHandler
        }

        fun startWith(state: S): StateMachineImpl<E, S, ASF, USF> {
            return StateMachineImpl(
                state,
                eventHandler,
                asyncSideEffectHandler,
                analyticsEventHandler
            )
        }
    }
}
