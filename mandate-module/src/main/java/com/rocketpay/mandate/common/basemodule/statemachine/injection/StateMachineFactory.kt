package com.rocketpay.mandate.common.basemodule.statemachine.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.common.mvistatemachine.contract.Event
import com.rocketpay.mandate.common.mvistatemachine.contract.State
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.StateMachine

@Suppress("UNCHECKED_CAST")
internal open class StateMachineFactory<E: Event, S: State, USF: UiSideEffect>(private val stateMachine: StateMachine<E, S, USF>): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StateMachine::class.java) -> stateMachine as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
