package com.udharpay.main.app.presentation.ui.view

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.main.init.MandateLauncher
import com.udharpay.R
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var fragment: Fragment? = null
    private var insetView: View? = null

    var btnOpen: Button? = null
    var etReference1: EditText? = null
    var btnMandateAdd: Button? = null
    var etReference2: EditText? = null
    var btnMandateDetails: Button? = null
    var btnLogout: Button? = null

    private var referenceId1: String = ""
    private var referenceId2: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.MyMainScreen)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        registerBackStackChangedListener()
        initValueFromRemoteConfig {
            handleAppLock()
            if (savedInstanceState == null) {
            }
        }
    }

    override fun onStart() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, windowInsets ->
            this.insetView = view
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    or WindowInsetsCompat.Type.displayCutout() or WindowInsetsCompat.Type.ime())
            setInsetBackgroundColor(getColor(R.color.blue_1))
            view.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        super.onStart()
        btnOpen = findViewById(R.id.btnOpen)
        etReference1 = findViewById(R.id.etReference1)
        btnMandateAdd = findViewById(R.id.btnMandateAdd)
        etReference2 = findViewById(R.id.etReference2)
        btnMandateDetails = findViewById(R.id.btnMandateDetails)
        btnOpen?.setOnClickListener {
            MandateLauncher.openMandateList(this)
        }
        etReference1?.addTextChangedListener {
            referenceId1 = it.toString()
        }
        btnMandateAdd?.setOnClickListener {
            MandateLauncher.openMandateCreation(this,referenceId1)
        }
        etReference2?.addTextChangedListener {
            referenceId2 = it.toString()
        }
        btnMandateDetails?.setOnClickListener {
            MandateLauncher.openMandateDetails(this,referenceId2)
        }
        btnLogout = findViewById(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            lifecycleScope.launch{
                MandateLauncher.logoutUser()
            }
        }

        FragmentResultBus.register(MandateLauncher.HELP_ACTION_TRIGGERED) {
            Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show()

        }
    }

    private fun setInsetBackgroundColor(colorId: Int) {
        try {
            window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val gd = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(colorId, getColor(R.color.grey_6))
            )
            insetView?.background = gd
            window?.statusBarColor = colorId
        }catch (ex: Exception){

        }
    }

    private fun initValueFromRemoteConfig(launchFragmentA: () -> Unit) {
        launchFragmentA()
    }
    private fun handleAppLock() {
    }

    private fun registerBackStackChangedListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.fragments.isNotEmpty()) {
                fragment = supportFragmentManager.fragments.last()
            }
        }
    }



    var pressAgainToExit = true
    private val timer = object : CountDownTimer(5000, 1000) {
        override fun onFinish() { pressAgainToExit = true }
        override fun onTick(millisUntilFinished: Long) {}
    }

}
