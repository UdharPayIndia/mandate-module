package com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import co.hyperverge.hyperkyc.HyperKyc
import co.hyperverge.hyperkyc.data.models.HyperKycConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class RpKycActivity : AppCompatActivity() {

    companion object Companion {
        const val CONFIG = "config"
        const val OWNER_IDENTITY_SUMITTED = "owner_identity_submitted"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

    override fun onStart() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
            view.setBackgroundColor(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        super.onStart()
    }

}
