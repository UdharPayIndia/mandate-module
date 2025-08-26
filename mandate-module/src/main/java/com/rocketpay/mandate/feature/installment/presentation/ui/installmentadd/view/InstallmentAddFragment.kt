package com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.view

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentInstallmentAddRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.InstallmentAddUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.statemachine.OtpTimer
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentadd.viewmodel.InstallmentAddUM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DatePickerUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class InstallmentAddFragment : BaseMainFragment<InstallmentAddEvent, InstallmentAddState, InstallmentAddUSF>() {

    private lateinit var binding: FragmentInstallmentAddRpBinding
    private lateinit var vm: InstallmentAddUM
    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    private var otpTimer: OtpTimer? = null

    companion object {
        const val MANDATE_ID = "mandate_id"
        fun newInstance(bundle: Bundle?): InstallmentAddFragment {
            val installmentAddFragment = InstallmentAddFragment()
            installmentAddFragment.arguments = bundle
            return installmentAddFragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, installmentStateMachineFactory)[InstallmentAddStateMachine::class.java]
        vm = InstallmentAddUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val mandateId = savedInstanceState?.getString(MANDATE_ID)
        stateMachine.dispatchEvent(InstallmentAddEvent.LoadData(mandateId))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInstallmentAddRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.toolbarIcon.get()?.setColorFilter(ResourceManager.getInstance().getColor(R.color.rp_blue_2), PorterDuff.Mode.SRC_ATOP)
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_blue_2))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_grey_6))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_create_new_installment))
        setupToolbar(vm)
        binding.vm = vm
        stateMachine.dispatchEvent(InstallmentAddEvent.AmountFocusChanged)
    }

    override fun handleState(state: InstallmentAddState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: InstallmentAddUSF) {
        when (sideEffect) {
            is InstallmentAddUSF.ShowInProgress -> {
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is InstallmentAddUSF.StartSmsListener -> {
                progressDialog.dismiss()
                handleGetSms(sideEffect.interval, sideEffect.otpTimeout)
                stateMachine.dispatchEvent(InstallmentAddEvent.OtpFocusChanged)
            }
            is InstallmentAddUSF.CloseClick -> {
                progressDialog.dismiss()
                onBackPress()
            }
            is InstallmentAddUSF.ShowError -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
            }
            is InstallmentAddUSF.CloseProgressDialog -> {
                progressDialog.dismiss()
            }
            is InstallmentAddUSF.AmountFocusChanged -> {
                binding.etMobileNumber.requestFocus()
                KeyboardUtils.showKeyBoard(binding.etMobileNumber, requireContext())
            }
            is InstallmentAddUSF.OtpFocusChanged -> {
                binding.etOtp.requestFocus()
                KeyboardUtils.showKeyBoard(binding.etOtp, requireContext())
            }
            is InstallmentAddUSF.OpenStartDateSelection -> {
                DatePickerUtils.showDatePicker(requireContext(), minDate = DateUtils.getDate(DateUtils.addDay(System.currentTimeMillis(), 1), DateUtils.SLASH_DATE_FORMAT), addRemoveButton = false) { _, time ->
                    stateMachine.dispatchEvent(InstallmentAddEvent.DueDateSelected(time))
                }
            }
        }
    }

    private fun handleGetSms(interval: Long, otpTimeout: Long) {
        if (otpTimer == null) {
            otpTimer = OtpTimer(
                otpTimeout = otpTimeout,
                interval = interval,
                {
                    stateMachine.dispatchEvent(InstallmentAddEvent.UpdateTimeLeft(it))
                },
                {
                    otpTimer?.cancel()
                    stateMachine.dispatchEvent(InstallmentAddEvent.OtpTimeout)
                }
            )
        }
        otpTimer?.cancel()
        otpTimer?.start()
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onDestroy() {
        otpTimer?.cancel()
        super.onDestroy()
    }
}
