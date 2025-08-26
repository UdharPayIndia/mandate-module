package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.view

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentEnterPenaltyAmountRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountState
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine.EnterPenaltyAmountUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.penalty.viewmodel.EnterPenaltyAmountUM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import javax.inject.Inject

internal class EnterPenaltyAmountFragment :
    StateMachineBottomSheetFragment<EnterPenaltyAmountEvent, EnterPenaltyAmountState, EnterPenaltyAmountUSF>() {

    private lateinit var binding: FragmentEnterPenaltyAmountRpBinding
    private lateinit var vm: EnterPenaltyAmountUM

    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    private val confirmDialog by lazy { ProgressDialog(requireContext(), vm.confirmDialogVM) }

    companion object {
        const val BUNDLE_MANDATE_ID = "BUNDLE_MANDATE_ID"
        const val BUNDLE_INSTALLMENT_ID = "BUNDLE_INSTALLMENT_ID"
        const val BUNDLE_INSTALLMENT_AMOUNT = "BUNDLE_INSTALLMENT_AMOUNT"
        fun newInstance(bundle: Bundle?): EnterPenaltyAmountFragment {
            val fragment = EnterPenaltyAmountFragment()
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
            this,
            installmentStateMachineFactory
        )[EnterPenaltyAmountStateMachine::class.java]
        vm = EnterPenaltyAmountUM { stateMachine.dispatchEvent(it) }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        expandFull(false)
        binding = FragmentEnterPenaltyAmountRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val mandateId = savedInstanceState?.getString(BUNDLE_MANDATE_ID, "").orEmpty()
        val installmentId = savedInstanceState?.getString(BUNDLE_INSTALLMENT_ID, "").orEmpty()
        val installmentAmount = savedInstanceState?.getString(BUNDLE_INSTALLMENT_AMOUNT, "").orEmpty()
        stateMachine.dispatchEvent(EnterPenaltyAmountEvent.Init(mandateId, installmentId, installmentAmount))
    }


    override fun handleState(state: EnterPenaltyAmountState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: EnterPenaltyAmountUSF) {
        when (sideEffect) {
            is EnterPenaltyAmountUSF.ShowPenaltyConfirmation -> {
                vm.confirmDialogVM.setInitState(
                    headerIcon = sideEffect.headerDrawable,
                    headerBackground = sideEffect.headerBackground,
                    titleText = SpannableString(sideEffect.title),
                    detailText = sideEffect.detail,
                    actionText = sideEffect.actionText,
                    secondaryBtnText = sideEffect.secondaryBtnText
                )
                confirmDialog.show()
            }
            is EnterPenaltyAmountUSF.ShowProgressDialog -> {
                confirmDialog.dismiss()
                vm.progressDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
                progressDialog.show()
            }
            is EnterPenaltyAmountUSF.ShowErrorDialog -> {
                confirmDialog.dismiss()
                vm.progressDialogVM.setErrorState(sideEffect.title, sideEffect.detail)
                progressDialog.show()
            }
            is EnterPenaltyAmountUSF.DismissConfirmDialog -> {
                confirmDialog.dismiss()
            }
            is EnterPenaltyAmountUSF.DismissProgressDialog -> {
                progressDialog.dismiss()
            }
            is EnterPenaltyAmountUSF.CloseScreen -> {
                progressDialog.dismiss()
                confirmDialog.dismiss()
                dismiss()
            }
        }
    }
}
