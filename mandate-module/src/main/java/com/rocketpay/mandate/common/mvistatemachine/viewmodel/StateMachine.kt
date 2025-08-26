package com.rocketpay.mandate.common.mvistatemachine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rocketpay.mandate.common.mvistatemachine.contract.Event
import com.rocketpay.mandate.common.mvistatemachine.contract.State
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal abstract class StateMachine<E: Event, S: State, USF: UiSideEffect>: ViewModel()  {

    abstract fun dispatchEvent(event: E)

    abstract fun getState(): LiveData<S>

    abstract fun getUiSideEffect(): LiveData<USF>
}
