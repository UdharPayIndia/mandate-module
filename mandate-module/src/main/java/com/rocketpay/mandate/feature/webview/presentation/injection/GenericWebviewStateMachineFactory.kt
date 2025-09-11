package com.rocketpay.mandate.feature.webview.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.feature.webview.presentation.ui.statemachine.GenericWebviewSimpleStateMachine

@Suppress("UNCHECKED_CAST")
internal open class GenericWebviewStateMachineFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GenericWebviewSimpleStateMachine::class.java) -> GenericWebviewSimpleStateMachine() as T
            else -> throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
