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
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentLoginRpBinding
import com.rocketpay.mandate.feature.login.presentation.injection.LoginComponent
import com.rocketpay.mandate.feature.login.presentation.injection.LoginStateMachineFactory
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginEvent
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginState
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginStateMachine
import com.rocketpay.mandate.feature.login.presentation.ui.login.statemachine.LoginUSF
import com.rocketpay.mandate.feature.login.presentation.ui.login.viewmodel.LoginUM
import javax.inject.Inject

internal class LoginFragment : BaseMainFragment<LoginEvent, LoginState, LoginUSF>() {

    private lateinit var binding: FragmentLoginRpBinding
    private lateinit var vm: LoginUM
    @Inject
    internal lateinit var loginStateMachineFactory: LoginStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
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
        }
    }


    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

}
