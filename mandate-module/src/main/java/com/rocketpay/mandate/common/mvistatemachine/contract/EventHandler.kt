package com.rocketpay.mandate.common.mvistatemachine.contract

internal abstract class EventHandler<E : Event, S : State, ASF : AsyncSideEffect, USF : UiSideEffect> {

    abstract fun handleEvent(event: E, state: S): Next<S?, ASF?, USF?>

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

    private fun next(state: S?, asyncSideEffect: ASF?, uiSideEffect: USF?): Next<S?, ASF?, USF?> {
        return Next(state, asyncSideEffect, uiSideEffect)
    }
}
