package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentBankAccountAddRpBinding
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountComponent
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountStateMachineFactory
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddEvent
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddState
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddStateMachine
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddUSF
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.viewmodel.BankAccountAddUM
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class BankAccountAddFragment: BaseMainFragment<BankAccountAddEvent, BankAccountAddState, BankAccountAddUSF>() {

    private lateinit var binding: FragmentBankAccountAddRpBinding
    private lateinit var vm: BankAccountAddUM
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    @Inject
    internal lateinit var bankAccountStateMachineFactory: BankAccountStateMachineFactory

    companion object {
        const val BANK_ACCOUNT_ADDED = "bank_account_added"
        const val IS_FROM_ONBOARDING = "is_from_onboarding"
        const val BUNDLE_SOURCE = "BUNDLE_SOURCE"
        fun newInstance(bundle: Bundle): BankAccountAddFragment {
            val bankAccountAddFragment = BankAccountAddFragment()
            bankAccountAddFragment.arguments = bundle
            return bankAccountAddFragment
        }
    }

    override fun injection() {
        super.injection()
        BankAccountComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, bankAccountStateMachineFactory)[BankAccountAddStateMachine::class.java]
        vm = BankAccountAddUM { stateMachine.dispatchEvent(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_support_white_rp, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_support ->{
                handleContactUsClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBankAccountAddRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val isFromOnBoarding = savedInstanceState?.getBoolean(IS_FROM_ONBOARDING, false) ?: false
        val source = savedInstanceState?.getString(BUNDLE_SOURCE, "").orEmpty()
        stateMachine.dispatchEvent(BankAccountAddEvent.LoadData(isFromOnBoarding, source))
    }

    override fun initView() {
        super.initView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(getString(R.string.rp_add_bank_account))
        setupToolbar(vm)
        binding.vm = vm
        stateMachine.dispatchEvent(BankAccountAddEvent.AccountNumberFocusChange)
    }

    override fun handleState(state: BankAccountAddState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: BankAccountAddUSF) {
        when (sideEffect) {
            is BankAccountAddUSF.AddBankAccountSuccess -> {
                vm.progressDialogVM.setSuccessState(sideEffect.header,
                    sideEffect.message,
                    sideEffect.primaryButtonText,
                    sideEffect.secondaryButtonText
                )
            }
            is BankAccountAddUSF.AddBankAccountInProgress -> {
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is BankAccountAddUSF.AddBankAccountFail -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
            }
            is BankAccountAddUSF.CloseProgressDialog -> {
                progressDialog.dismiss()
            }
            is BankAccountAddUSF.GotoNextScreen -> {
                FragmentResultBus.fire(BANK_ACCOUNT_ADDED, true)
                progressDialog.dismiss()
                onBackPress()
            }
            BankAccountAddUSF.MoveNameFieldUp -> {
                updatePosition(binding.scrollView)
            }
            BankAccountAddUSF.MoveUpiIdFieldUp -> {
                updatePosition(binding.scrollView)
            }
            BankAccountAddUSF.AccountNumberFocusChange -> {
                binding.etAccountNumber.requestFocus()
                KeyboardUtils.showKeyBoard(binding.etAccountNumber, requireContext())
            }
        }
    }

    private fun updatePosition(scrollView: NestedScrollView) {
        //scrollView.smoothScrollTo(0, scrollView.height)
    }

    override fun deInitView() {
        super.deInitView()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }
}
