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
            .setSecretToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjAwMDAwMTk4LWUxNzYtNjYwMi05ZGIxLWJkMTkxNjUyMDI2YyIsIm1vYmlsZV9udW1iZXIiOiIrOTE5NDAzMzgyNDIyIiwibWVyY2hhbnRfaWQiOiIwMDAwMDE5OS0zNDAzLTg0ZDUtYmM0My00OTgzZGM2OTgxNGUiLCJyb2xlIjoiQURNSU4iLCJhY2NvdW50X3R5cGUiOiJFTlRFUlBSSVNFIiwic291cmNlIjoiQ09NTU9OX0xPR0lOIiwiaWF0IjoxNzU3NTE4NTEzfQ.Hd0E6y146AoBW-oL4ul_K2s9mrWaTCSAHzOj-coXZS0")
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
