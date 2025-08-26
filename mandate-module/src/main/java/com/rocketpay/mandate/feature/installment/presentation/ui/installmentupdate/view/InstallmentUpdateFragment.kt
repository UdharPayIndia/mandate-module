package com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentInstallmentUpdateRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateState
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.statemachine.InstallmentUpdateUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentupdate.viewmodel.InstallmentUpdateUM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import javax.inject.Inject

internal class InstallmentUpdateFragment:
    StateMachineBottomSheetFragment<InstallmentUpdateEvent, InstallmentUpdateState, InstallmentUpdateUSF>() {

    private lateinit var binding: FragmentInstallmentUpdateRpBinding
    private lateinit var vm: InstallmentUpdateUM

    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    companion object {
        const val BUNDLE_INSTALLMENT_ID = "BUNDLE_INSTALLMENT_ID"
        const val BUNDLE_MODE = "BUNDLE_MODE"
        const val BUNDLE_COMMENT = "BUNDLE_COMMENT"

        fun newInstance(bundle: Bundle?): InstallmentUpdateFragment {
            val fragment = InstallmentUpdateFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.RpAdjustResizeBottomSheetTheme)
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(
            this, installmentStateMachineFactory)[InstallmentUpdateStateMachine::class.java]
        vm = InstallmentUpdateUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val installmentId = savedInstanceState?.getString(BUNDLE_INSTALLMENT_ID)
        val mode = savedInstanceState?.getString(BUNDLE_MODE)
        val comment = savedInstanceState?.getString(BUNDLE_COMMENT)
        stateMachine.dispatchEvent(InstallmentUpdateEvent.InitData(installmentId, mode, comment))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentInstallmentUpdateRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }


    override fun handleState(state: InstallmentUpdateState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: InstallmentUpdateUSF) {
        when (sideEffect) {
            is InstallmentUpdateUSF.ShowToast -> {
                progressDialog.dismiss()
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }

            is InstallmentUpdateUSF.ShowProgressDialog -> {
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }

            InstallmentUpdateUSF.CancelProgressDialog -> {
                progressDialog.dismiss()
            }
            is InstallmentUpdateUSF.CloseScreen -> {
                progressDialog.dismiss()
                dismiss()
            }
        }
    }
}
