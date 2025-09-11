package com.rocketpay.mandate.feature.webview.presentation.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

internal class GenericWebViewCommunicatorVM: ViewModel() {
    val communicateToWebView = MutableLiveData<String?>()
}