package com.udharpay.main.app.presentation.ui.view

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.multidex.MultiDexApplication
import com.rocketpay.mandate.main.init.MandateManager

class Application: MultiDexApplication(), LifecycleOwner {

    override val lifecycle = LifecycleRegistry(this)

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        initMandateManager()
        lifecycle.currentState = Lifecycle.State.CREATED
    }

    private fun initMandateManager() {
        MandateManager.Builder()
            .setContext(applicationContext)
            .setEnterpriseId("00000198-bd64-6c3a-b857-49bc88b0ed9b")
            .skipKyc(true)
            .build()
    }


    override fun onTerminate() {
        super.onTerminate()
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }
}
