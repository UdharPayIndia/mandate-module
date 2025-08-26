package com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udharpay.kernel.kernelcommon.eventbus.EventBus
import com.udharpay.kernel.kernelcommon.eventbus.FlowEventBus
import com.rocketpay.mandate.common.mvistatemachine.liveevent.LiveEvent
import com.rocketpay.mandate.common.mvistatemachine.contract.*
import com.rocketpay.mandate.common.mvistatemachine.contract.AnalyticsEventHandler
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal abstract class SimpleStateMachineImpl<E : Event, S : State, ASF : AsyncSideEffect, USF : UiSideEffect>(private val analyticsEventHandler: AnalyticsEventHandler<E, S>? = null) : StateMachine<E, S, USF>() {

    private var _state: S = this.startState()

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
                val next = handleEvent(event, _state)
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
        handleAsyncSideEffect(sideEffect, dispatchEvent, viewModelScope)
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

    abstract fun startState(): S

    abstract fun handleEvent(event: E, state: S): Next<S?, ASF?, USF?>

    abstract suspend fun handleAsyncSideEffect(sideEffect: ASF, dispatchEvent: (E) -> Unit, viewModelScope: CoroutineScope)

    ///////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    protected fun noChange(): Next<S?, ASF?, USF?> {
        return next(null, null, null)
    }

    protected fun next(state: S): Next<S?, ASF?, USF?> {
        return next(state, null, null)
    }

    protected fun next(asyncSideEffect: ASF): Next<S?, ASF?, USF?> {
        return next(null, asyncSideEffect, null)
    }

    protected fun next(uiSideEffect: USF): Next<S?, ASF?, USF?> {
        return next(null, null, uiSideEffect)
    }

    protected fun next(state: S?, asyncSideEffect: ASF?): Next<S?, ASF?, USF?> {
        return next(state, asyncSideEffect, null)
    }

    protected fun next(state: S?, uiSideEffect: USF?): Next<S?, ASF?, USF?> {
        return next(state, null, uiSideEffect)
    }

    protected fun next(asyncSideEffect: ASF?, uiSideEffect: USF?): Next<S?, ASF?, USF?> {
        return Next(null, asyncSideEffect, uiSideEffect)
    }

    fun next(state: S?, asyncSideEffect: ASF?, uiSideEffect: USF?): Next<S?, ASF?, USF?> {
        return Next(state, asyncSideEffect, uiSideEffect)
    }
}
