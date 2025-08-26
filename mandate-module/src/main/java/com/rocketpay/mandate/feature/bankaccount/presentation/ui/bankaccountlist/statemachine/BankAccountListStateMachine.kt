package com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountlist.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.bankaccount.data.BankAccountSyncer
import com.rocketpay.mandate.feature.bankaccount.domain.usecase.BankAccountUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import kotlinx.coroutines.CoroutineScope

internal class BankAccountListStateMachine(
    private val bankAccountUseCase: BankAccountUseCase
) : SimpleStateMachineImpl<BankAccountListEvent, BankAccountListState, BankAccountListASF, BankAccountListUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): BankAccountListState {
        return BankAccountListState()
    }

    override fun handleEvent(
        event: BankAccountListEvent,
        state: BankAccountListState
    ): Next<BankAccountListState?, BankAccountListASF?, BankAccountListUSF?> {
        return when (event) {
            is BankAccountListEvent.LoadBankAccounts -> {
                next(BankAccountListASF.LoadBankAccountList)
            }
            is BankAccountListEvent.BankAccountListLoaded -> {
                next(state.copy(bankAccounts = event.bankAccounts), BankAccountListUSF.UpdateBankAccounts(event.bankAccounts))
            }
            is BankAccountListEvent.RefreshBankAccounts -> {
                next(BankAccountListASF.RefreshBankAccounts)
            }
            is BankAccountListEvent.AddBankAccount -> {
                next(BankAccountListUSF.GotoAddBankAccount)
            }
            is BankAccountListEvent.MenuClick -> {
                next(BankAccountListUSF.OpenBankAccountActionMenu(event.view, event.bankAccount))
            }
            is BankAccountListEvent.DeleteBankAccountClick -> {
                next(
                    state.copy(selectedBankAccount = event.bankAccount),
                    BankAccountListUSF.ShowDeleteBankConfirmationDialog(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_delete_filled),
                        ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                        ResourceManager.getInstance().getString(R.string.rp_are_you_sure_you_want_to_delete_this_account),
                        "",
                        ResourceManager.getInstance().getString(R.string.rp_delete),
                        ResourceManager.getInstance().getString(R.string.rp_cancel)
                    )
                )
            }
            is BankAccountListEvent.DeleteBankAccountConfirmed -> {
                val message = ResourceManager.getInstance().getString(R.string.rp_deleting_bank_account)
                next(BankAccountListASF.DeleteBankAccount(state.selectedBankAccount), BankAccountListUSF.ShowProgressDialog(message, ""))
            }
            is BankAccountListEvent.DeleteBankAccountSuccess -> {
                val message = ResourceManager.getInstance().getString(R.string.rp_bank_account_deleted_successfully)
                next(BankAccountListUSF.ShowSuccessMessage(message))
            }
            is BankAccountListEvent.DeleteBankAccountFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_bank_account_deletion_failed)
                val message = event.errorMessage
                next(BankAccountListUSF.ShowErrorDialog(header, message))
            }
            is BankAccountListEvent.SetBankAccountPrimaryClick -> {
                val message = ResourceManager.getInstance().getString(R.string.rp_setting_as_primary_account)
                next(BankAccountListASF.SetBankAccountPrimary(event.bankAccount), BankAccountListUSF.ShowProgressDialog(message, ""))
            }
            is BankAccountListEvent.PrimaryBankAccountSuccess -> {
                val message = ResourceManager.getInstance().getString(R.string.rp_bank_account_set_to_primary)
                next(BankAccountListUSF.ShowSuccessMessage(message))
            }
            is BankAccountListEvent.PrimaryBankAccountFailed -> {
                val header = ResourceManager.getInstance().getString(R.string.rp_bank_account_cannot_set_as_primary)
                val message = event.errorMessage
                next(BankAccountListUSF.ShowErrorDialog(header, message))
            }
            is BankAccountListEvent.CloseDialogClick -> {
                next(BankAccountListUSF.CloseProgressDialog)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: BankAccountListASF,
        dispatchEvent: (BankAccountListEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            BankAccountListASF.LoadBankAccountList -> {
                bankAccountUseCase.getBankAccounts().collectIn(viewModelScope) {
                    dispatchEvent(BankAccountListEvent.BankAccountListLoaded(it))
                }
            }
            BankAccountListASF.RefreshBankAccounts -> {
                SyncManager.getInstance().enqueue(BankAccountSyncer.TYPE)
            }
            is BankAccountListASF.DeleteBankAccount -> {
                sideEffect.bankAccount?.let {
                    when(val result = bankAccountUseCase.deleteBankAccount(it)){
                        is Outcome.Success -> {
                            dispatchEvent(BankAccountListEvent.DeleteBankAccountSuccess)
                        }
                        is Outcome.Error -> {
                            dispatchEvent(BankAccountListEvent.DeleteBankAccountFailed(result.error.message.orEmpty()))
                        }
                    }
                }
            }
            is BankAccountListASF.SetBankAccountPrimary -> {
                when(val result = bankAccountUseCase.markBankAccountAsPrimary(sideEffect.bankAccount)){
                    is Outcome.Success -> {
                        dispatchEvent(BankAccountListEvent.PrimaryBankAccountSuccess)
                    }
                    is Outcome.Error -> {
                        dispatchEvent(BankAccountListEvent.PrimaryBankAccountFailed(result.error.message.orEmpty()))
                    }
                }
            }
        }
    }
}
