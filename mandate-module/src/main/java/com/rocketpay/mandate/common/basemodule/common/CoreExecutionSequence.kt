package com.rocketpay.mandate.common.basemodule.common

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper

internal interface CoreExecutionSequence {

    fun create(arguments: Bundle?, savedInstanceState: Bundle?) {
        injection()
        loadData(arguments ?: savedInstanceState)
    }

    @CallSuper
    fun injection() {
    }

    @CallSuper
    fun loadData(savedInstanceState: Bundle?) {
    }

    fun viewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        registerListener()
    }

    @CallSuper
    fun initView() {
    }

    @CallSuper
    fun registerListener() {
    }

    fun destroyView() {
        unRegisterListener()
        deInitView()
    }

    @CallSuper
    fun unRegisterListener() {
    }

    @CallSuper
    fun deInitView() {
    }
}
