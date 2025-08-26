package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.view

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentBankAccountListRpBinding
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountComponent
import com.rocketpay.mandate.feature.bankaccount.presentation.injection.BankAccountStateMachineFactory
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.adapter.BankAccountAdapter
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListEvent
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListState
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListStateMachine
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine.BankAccountListUSF
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.viewmodel.BankAccountListUM
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class BankAccountListFragment : BaseMainFragment<BankAccountListEvent, BankAccountListState, BankAccountListUSF>() {

    @Inject
    internal lateinit var bankAccountAdapter: BankAccountAdapter
    private lateinit var binding: FragmentBankAccountListRpBinding
    private lateinit var vm: BankAccountListUM
    @Inject
    internal lateinit var bankAccountStateMachineFactory: BankAccountStateMachineFactory
    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }
    private val bankDeleteConfirmationDialog by lazy { ProgressDialog(requireContext(), vm.bankDeleteConfirmationDialogVM) }

    companion object {
        fun newInstance(bundle: Bundle?): BankAccountListFragment {
            val bankAccountListFragment = BankAccountListFragment()
            bankAccountListFragment.arguments = bundle
            return bankAccountListFragment
        }
    }

    override fun injection() {
        super.injection()
        BankAccountComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, bankAccountStateMachineFactory)[BankAccountListStateMachine::class.java]
        vm = BankAccountListUM { stateMachine.dispatchEvent(it) }
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

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(BankAccountListEvent.LoadBankAccounts)
        stateMachine.dispatchEvent(BankAccountListEvent.RefreshBankAccounts)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBankAccountListRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_bank_details))
        setupToolbar(vm)
        binding.vm = vm
        bankAccountAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = bankAccountAdapter
    }

    override fun registerListener() {
        super.registerListener()
        FragmentResultBus.register(BankAccountAddFragment.BANK_ACCOUNT_ADDED) {
        }
    }

    override fun handleState(state: BankAccountListState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: BankAccountListUSF) {
        when (sideEffect) {
            is BankAccountListUSF.UpdateBankAccounts -> {
                bankAccountAdapter.swapData(sideEffect.bankAccounts)
            }
            BankAccountListUSF.GotoAddBankAccount -> {
                val bundle = Bundle()
                bundle.putBoolean(BankAccountAddFragment.IS_FROM_ONBOARDING, false)
                bundle.putString(BankAccountAddFragment.BUNDLE_SOURCE, "bank_account_addition")
                listener?.onNavigate(BankAccountAddFragment.newInstance(bundle), fragmentTag = BankAccountAddScreen.name)
            }
            is BankAccountListUSF.OpenBankAccountActionMenu -> {
                handleOpenBankAccountActionMenu(sideEffect.view, sideEffect.bankAccount)
            }
            is BankAccountListUSF.ShowProgressDialog -> {
                bankDeleteConfirmationDialog.dismiss()
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
            is BankAccountListUSF.CloseProgressDialog -> {
                bankDeleteConfirmationDialog.dismiss()
                progressDialog.dismiss()
            }
            is BankAccountListUSF.ShowSuccessMessage -> {
                progressDialog.dismiss()
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is BankAccountListUSF.ShowErrorDialog -> {
                vm.progressDialogVM.setErrorState(sideEffect.header, sideEffect.message)
            }
            is BankAccountListUSF.ShowDeleteBankConfirmationDialog -> {
                vm.bankDeleteConfirmationDialogVM.setInitState(
                    headerIcon = sideEffect.headerDrawable,
                    headerBackground = sideEffect.headerBackground,
                    titleText = SpannableString(sideEffect.title),
                    detailText = sideEffect.detail,
                    actionText = sideEffect.actionText,
                    secondaryBtnText = sideEffect.secondaryBtnText
                )
                bankDeleteConfirmationDialog.show()
            }
        }
    }

    private fun handleOpenBankAccountActionMenu(view: View, bankAccount: BankAccount) {
        val popUpMenu = PopupMenu(context, view)
        val inflater = popUpMenu.menuInflater
        inflater.inflate(R.menu.menu_bank_account_options_rp, popUpMenu.menu)
        popUpMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete -> {
                    stateMachine.dispatchEvent(BankAccountListEvent.DeleteBankAccountClick(bankAccount))
                    return@setOnMenuItemClickListener true
                }
                R.id.action_primary -> {
                    stateMachine.dispatchEvent(BankAccountListEvent.SetBankAccountPrimaryClick(bankAccount))
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener true
            }
        }
        popUpMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        FragmentResultBus.unRegister(BankAccountAddFragment.BANK_ACCOUNT_ADDED)
    }
}
