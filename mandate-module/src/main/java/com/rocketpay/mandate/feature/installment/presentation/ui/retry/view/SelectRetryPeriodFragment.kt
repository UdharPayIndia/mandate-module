package com.rocketpay.mandate.feature.installment.presentation.ui.retry.view

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentSelectRetryPeriodRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodState
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.statemachine.SelectRetryPeriodUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.retry.viewmodel.SelectRetryPeriodUM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DatePickerUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import javax.inject.Inject

internal class SelectRetryPeriodFragment :
    StateMachineBottomSheetFragment<SelectRetryPeriodEvent, SelectRetryPeriodState, SelectRetryPeriodUSF>() {

    private lateinit var binding: FragmentSelectRetryPeriodRpBinding
    private lateinit var vm: SelectRetryPeriodUM

    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    private val confirmDialog by lazy { ProgressDialog(requireContext(), vm.confirmDialogVM) }

    companion object {
        const val BUNDLE_MANDATE_ID = "BUNDLE_MANDATE_ID"
        const val BUNDLE_INSTALLMENT_ID = "BUNDLE_INSTALLMENT_ID"
        fun newInstance(bundle: Bundle?): SelectRetryPeriodFragment {
            val fragment = SelectRetryPeriodFragment()
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
        )[SelectRetryPeriodStateMachine::class.java]
        vm = SelectRetryPeriodUM { stateMachine.dispatchEvent(it) }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        expandFull(false)
        binding = FragmentSelectRetryPeriodRpBinding.inflate(inflater, container, false)
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
        stateMachine.dispatchEvent(SelectRetryPeriodEvent.Init(mandateId, installmentId))
    }


    override fun handleState(state: SelectRetryPeriodState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: SelectRetryPeriodUSF) {
        when (sideEffect) {
            is SelectRetryPeriodUSF.OpenRetryDateSelection -> {
                val minDate = DateUtils.getDate(DateUtils.addDay(System.currentTimeMillis(), 1))
                DatePickerUtils.showDatePicker(requireContext(), minDate = minDate, addRemoveButton = false) { _, time ->
                    stateMachine.dispatchEvent(SelectRetryPeriodEvent.RetryDateSelected(time))
                }
            }
            is SelectRetryPeriodUSF.ShowRetryConfirmation -> {
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
            is SelectRetryPeriodUSF.ShowProgressDialog -> {
                confirmDialog.dismiss()
                vm.progressDialogVM.setProgressState(sideEffect.title, sideEffect.detail)
                progressDialog.show()
            }
            is SelectRetryPeriodUSF.ShowErrorDialog -> {
                confirmDialog.dismiss()
                vm.progressDialogVM.setErrorState(sideEffect.title, sideEffect.detail)
                progressDialog.show()
            }
            is SelectRetryPeriodUSF.DismissConfirmDialog -> {
                confirmDialog.dismiss()
            }
            is SelectRetryPeriodUSF.DismissProgressDialog -> {
                progressDialog.dismiss()
            }
            is SelectRetryPeriodUSF.CloseScreen -> {
                progressDialog.dismiss()
                confirmDialog.dismiss()
                dismiss()
            }
        }
    }
}
