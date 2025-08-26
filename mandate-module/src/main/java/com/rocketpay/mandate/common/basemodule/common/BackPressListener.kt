package com.rocketpay.mandate.common.basemodule.common

internal interface BackPressListener {

    fun isBackPressHandled(): Boolean

    fun onBackPress()
}
