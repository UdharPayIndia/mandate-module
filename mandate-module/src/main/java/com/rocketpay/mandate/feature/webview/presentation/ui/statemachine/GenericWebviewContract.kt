package com.rocketpay.mandate.feature.webview.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class GenericWebviewState(
    val url: String? = null,
    val toolbarTitle: String? = null,
    val shouldEnableCache: Boolean = true
) : BaseState(GenericWebviewScreen)


internal sealed class GenericWebviewEvent(name: String? = null) : BaseEvent(name) {
    data class LoadPage(val url: String?) : GenericWebviewEvent()
    data class LoadUi(val toolbarTitle: String?, val shouldEnableCache: Boolean?) : GenericWebviewEvent()
    data class HandleAction(val action: String, val data: String?, val secret: String?) : GenericWebviewEvent()
}


internal sealed class GenericWebviewASF : AsyncSideEffect {
    object GenericWebviewASF1 : GenericWebviewASF()
}


internal sealed class GenericWebviewUSF : UiSideEffect {
    data class LoadUrl(val url: String) : GenericWebviewUSF()
    data class LoadUi(val shouldEnableCache: Boolean) : GenericWebviewUSF()
    data class HandleSpecificAction(val action: String, val data: String?) : GenericWebviewUSF()
}

internal object GenericWebviewScreen : Screen("generic_webview")