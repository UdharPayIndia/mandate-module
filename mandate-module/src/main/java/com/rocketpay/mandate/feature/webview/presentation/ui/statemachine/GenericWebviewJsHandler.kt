package com.rocketpay.mandate.feature.webview.presentation.ui.statemachine

import android.webkit.JavascriptInterface

internal open class GenericWebviewJsHandler(val actionHandler: (action: String, data: String?, secret: String?) -> Unit,
                                   val paymentFinished: () -> Unit) {

    companion object {
        const val TAG = "GenericWebviewJsHandler"
    }

    @JavascriptInterface
    fun onActionHandler(action: String, data: String?, secret: String?) {
        actionHandler(action, data, secret)
    }

    @JavascriptInterface
    fun onPaymentFinished() {
        paymentFinished()
    }
}
