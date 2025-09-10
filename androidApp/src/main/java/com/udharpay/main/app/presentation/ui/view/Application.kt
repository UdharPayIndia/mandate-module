package com.udharpay.main.app.presentation.ui.view

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.multidex.MultiDexApplication
import com.rocketpay.mandate.main.init.MandateManager
import com.udharpay.BuildConfig

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
            .setEnterpriseId(BuildConfig.ENTERPRISE_ID)
            .setEnterpriseToken(BuildConfig.ENTERPRISE_TOKEN)
            .setAppName("EMI Safe Pro")
            .skipKyc(true)
            .isDebug(true)
            .build()
    }


    override fun onTerminate() {
        super.onTerminate()
        lifecycle.currentState = Lifecycle.State.DESTROYED
    }
}
