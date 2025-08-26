package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.hyperverge.hyperkyc.HyperKyc
import co.hyperverge.hyperkyc.data.models.HyperKycConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus

internal class RpKycActivity : AppCompatActivity() {

    companion object Companion {
        const val CONFIG = "config"
        const val OWNER_IDENTITY_SUMITTED = "owner_identity_submitted"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_rp)
        val config = intent.extras?.getSerializable(CONFIG)
        if (config != null) {
            try {
                hyperKycLauncher.launch(config as HyperKycConfig)
            } catch (e: Exception) {
                finish()
            }
        } else {
            finish()
        }
    }

    private val hyperKycLauncher = registerForActivityResult(HyperKyc.Contract()) { result ->
        FragmentResultBus.fire(OWNER_IDENTITY_SUMITTED, result)
        finish()
    }
}
