package com.rocketpay.mandate.feature.login.presentation.ui.login.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.databinding.FragmentLoginRpBinding
import com.rocketpay.mandate.feature.login.presentation.injection.LoginComponent
import com.rocketpay.mandate.feature.login.presentation.injection.LoginStateMachineFactory
import com.rocketpay.mandate.feature.login.presentation.service.smsretriver.SmsRetrieverListener
import com.rocketpay.mandate.feature.login.presentation.service.smsretriver.SmsRetrieverService
import com.rocketpay.mandate.feature.login.presentation.service.smsretriver.SmsRetrieverServiceImpl
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginEvent
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginState
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginStateMachine
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginUSF
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.OtpTimer
import com.rocketpay.mandate.feature.login.presentation.ui.login.viewmodel.LoginUM
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.PhoneUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class LoginFragment : BaseMainFragment<LoginEvent, LoginState, LoginUSF>() {

    private lateinit var binding: FragmentLoginRpBinding
    private lateinit var vm: LoginUM
    @Inject
    internal lateinit var loginStateMachineFactory: LoginStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    private var otpTimer: OtpTimer? = null
    private var smsRetrieverService: SmsRetrieverService? = null

    companion object {
        const val RESULT_LOGGED_IN = "RESULT_LOGGED_IN"
        fun newInstance(bundle: Bundle?): LoginFragment {
            val loginFragment = LoginFragment()
            loginFragment.arguments = bundle
            return loginFragment
        }
    }

    override fun injection() {
        super.injection()
        LoginComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, loginStateMachineFactory)[LoginStateMachine::class.java]
        vm = LoginUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(LoginEvent.Init)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.toolbarIcon.get()?.setColorFilter(ResourceManager.getInstance().getColor(R.color.rp_blue_2), PorterDuff.Mode.SRC_ATOP)
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_grey_6))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_login))
        setupToolbar(vm)
        binding.vm = vm
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_support_rp, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_support ->{
                handleContactUsClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun handleState(state: LoginState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: LoginUSF) {
        when (sideEffect) {
            is LoginUSF.ShowInProgress -> {
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is LoginUSF.StartSmsListener -> {
                progressDialog.dismiss()
                handleGetSms(sideEffect.interval, sideEffect.otpTimeout)
                stateMachine.dispatchEvent(LoginEvent.OtpFocusChanged)
            }
            is LoginUSF.GotoHome -> {
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                progressDialog.dismiss()
                FragmentResultBus.fire(RESULT_LOGGED_IN, sideEffect.isKyced)
            }
            is LoginUSF.ShowError -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is LoginUSF.CloseProgressDialog -> {
                progressDialog.dismiss()
            }
            is LoginUSF.MobileNumberFocusChanged -> {
                binding.etMobileNumber.requestFocus()
                KeyboardUtils.showKeyBoard(binding.etMobileNumber, requireContext())
            }
            is LoginUSF.OtpFocusChanged -> {
                binding.etOtp.requestFocus()
                KeyboardUtils.showKeyBoard(binding.etOtp, requireContext())
            }
            is LoginUSF.RequestPhoneHint -> {
                requestPhoneNumberHint()
            }
        }
    }

    private fun requestPhoneNumberHint() {
        val hintRequest = GetPhoneNumberHintIntentRequest.builder().build()
        Identity.getSignInClient(requireActivity())
            .getPhoneNumberHintIntent(hintRequest)
            .addOnSuccessListener {
                phoneNumberHintIntentResultLauncher.launch(
                    IntentSenderRequest.Builder(it.intentSender).build()
                )
            }
            .addOnFailureListener {
            }
    }

    private val phoneNumberHintIntentResultLauncher: ActivityResultLauncher<IntentSenderRequest> = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val phoneNumber = Identity.getSignInClient(requireActivity()).getPhoneNumberFromIntent(result.data)
            if (!phoneNumber.isNullOrEmpty()) {
                binding.etMobileNumber.setText(PhoneUtils.removedCountryCodeFromMobileNumber(phoneNumber))
                stateMachine.dispatchEvent(LoginEvent.PhoneHintReceived(phoneNumber))
                stateMachine.dispatchEvent(LoginEvent.RequestOtpClick)
            } else {
                stateMachine.dispatchEvent(LoginEvent.MobileNumberFocusChanged)
            }
        } catch (e: Exception) {
        }
    }

    private fun handleGetSms(interval: Long, otpTimeout: Long) {
        if (otpTimer == null) {
            otpTimer = OtpTimer(
                otpTimeout = otpTimeout,
                interval = interval,
                {
                    stateMachine.dispatchEvent(LoginEvent.UpdateTimeLeft(it))
                },
                {
                    otpTimer?.cancel()
                    smsRetrieverService?.stopService()
                    stateMachine.dispatchEvent(LoginEvent.OtpTimeout)
                }
            )
        }
        otpTimer?.cancel()
        otpTimer?.start()

        if (smsRetrieverService == null) {
            smsRetrieverService =
                SmsRetrieverServiceImpl(requireContext(), object : SmsRetrieverListener {
                    override fun onSmsReceived(otp: String?) {
                        if (!otp.isNullOrEmpty()) {
                            otpTimer?.cancel()
                            smsRetrieverService?.stopService()
                            binding.etOtp.setText(otp)
                            stateMachine.dispatchEvent(LoginEvent.OtpHintReceived(otp))
                            stateMachine.dispatchEvent(LoginEvent.VerifyOtpClick)
                        }
                    }
                })
        }
        smsRetrieverService?.startService()
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestroy() {
        otpTimer?.cancel()
        smsRetrieverService?.stopService()
        super.onDestroy()
    }
}
