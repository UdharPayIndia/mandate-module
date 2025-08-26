package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine

import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.main.init.MandateManager
import kotlinx.coroutines.CoroutineScope

internal class EnterPenaltyAmountStateMachine  (
    private val installmentUseCase: InstallmentUseCase
): SimpleStateMachineImpl<EnterPenaltyAmountEvent, EnterPenaltyAmountState, EnterPenaltyAmountASF, EnterPenaltyAmountUSF>(
    EnterPenaltyAmountAnalyticsHandler()
) {

    override fun startState(): EnterPenaltyAmountState {
        return EnterPenaltyAmountState()
    }

    override fun handleEvent(
        event: EnterPenaltyAmountEvent,
        state: EnterPenaltyAmountState
    ): Next<EnterPenaltyAmountState?, EnterPenaltyAmountASF?, EnterPenaltyAmountUSF?> {
        return when (event) {
            is EnterPenaltyAmountEvent.Init -> {
                next(
                    state.copy(
                        mandateId = event.mandateId,
                        installmentId = event.installmentId,
                        installmentAmount = event.installmentAmount)
                )
            }
            is EnterPenaltyAmountEvent.UpdatePenaltyAmount -> {
                val penaltyAmount = AmountUtils.stringToDouble(event.penaltyAmount)
                val installmentAmount = AmountUtils.stringToDouble(state.installmentAmount)
                val minimumAmount = state.minimumAmount
                if(penaltyAmount <= 0.0){
                    next(state.copy(penaltyAmount = event.penaltyAmount,
                        penaltyAmountError = ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_be_greater_than_zero),
                        isEnabled = false))
                }else if(penaltyAmount < minimumAmount){
                    next(state.copy(penaltyAmount = event.penaltyAmount,
                        penaltyAmountError = ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_be_greater_than_amount,
                            AmountUtils.format(minimumAmount)),
                        isEnabled = false))
                }else if(penaltyAmount > installmentAmount){
                    next(state.copy(penaltyAmount = event.penaltyAmount,
                        penaltyAmountError = ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_be_lesser_than_installment_amount,
                            AmountUtils.format(installmentAmount)),
                        isEnabled = false))
                }else {
                    next(state.copy(penaltyAmount = event.penaltyAmount,
                        penaltyAmountError = "", isEnabled = true))
                }
                
            }
            is EnterPenaltyAmountEvent.SubmitPenalty -> {
                next(
                    EnterPenaltyAmountUSF.ShowPenaltyConfirmation(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle),
                        ResourceManager.getInstance().getDrawable(R.color.rp_yellow_1),
                        ResourceManager.getInstance().getString(R.string.rp_confirm_penalty),
                        ResourceManager.getInstance().getString(
                            R.string.rp_allow_app_to_charge_bounce_charge,
                            AmountUtils.format(AmountUtils.stringToDouble(state.penaltyAmount)),
                            MandateManager.getInstance().getAppName()
                        ),
                        ResourceManager.getInstance().getString(R.string.rp_yes),
                        ResourceManager.getInstance().getString(R.string.rp_dismiss)
                    )
                )
            }
            is EnterPenaltyAmountEvent.ChargePenaltyConfirmed -> {
                next(
                    EnterPenaltyAmountASF.ChargePenalty(
                        state.mandateId,
                        state.installmentId,
                        state.penaltyAmount
                    ),
                    EnterPenaltyAmountUSF.ShowProgressDialog(
                        ResourceManager.getInstance().getString(R.string.rp_initaiting_chrge_penalty),
                        ResourceManager.getInstance()
                            .getString(R.string.rp_skipping_installment_detail),
                    )
                )
            }
            is EnterPenaltyAmountEvent.CloseProgressDialog -> {
                next(EnterPenaltyAmountUSF.DismissProgressDialog)
            }
            is EnterPenaltyAmountEvent.ChargePenaltyDismiss -> {
                next(EnterPenaltyAmountUSF.DismissConfirmDialog)
            }
            is EnterPenaltyAmountEvent.ChargePenaltyFailed -> {
                next(
                    EnterPenaltyAmountUSF.ShowErrorDialog(
                        ResourceManager.getInstance().getString(R.string.rp_charge_penalty_failed),
                        event.errorMessage
                    )
                )
            }
            is EnterPenaltyAmountEvent.ChargePenaltySucceed -> {
                next(EnterPenaltyAmountASF.LoadPenalty(state.installmentId))
            }
            is EnterPenaltyAmountEvent.UpdatePenaltyDetails -> {
                next(EnterPenaltyAmountUSF.CloseScreen)
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: EnterPenaltyAmountASF,
        dispatchEvent: (EnterPenaltyAmountEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when(sideEffect){
            is EnterPenaltyAmountASF.ChargePenalty -> {
                when (val outcome = installmentUseCase.chargePenalty(
                    sideEffect.installmentId,
                    AmountUtils.stringToDouble(sideEffect.installmentAmount))
                ) {
                    is Outcome.Error -> dispatchEvent(
                        EnterPenaltyAmountEvent.ChargePenaltyFailed(
                            outcome.error.code.orEmpty(),
                            outcome.error.message.orEmpty()
                        )
                    )

                    is Outcome.Success -> {
                        installmentUseCase.fetchInstallmentActions(sideEffect.installmentId)
                        dispatchEvent(EnterPenaltyAmountEvent.ChargePenaltySucceed)
                    }
                }
            }
            is EnterPenaltyAmountASF.LoadPenalty -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                when (val outcome = installmentUseCase.fetchInstallmentPenalty(sideEffect.installmentId)) {
                    is Outcome.Error -> {
                        EnterPenaltyAmountEvent.ChargePenaltyFailed(
                            outcome.error.code.orEmpty(),
                            outcome.error.message.orEmpty()
                        )
                    }
                    is Outcome.Success -> {
                        dispatchEvent(EnterPenaltyAmountEvent.UpdatePenaltyDetails(outcome.data))
                    }
                }
            }
        }
    }

}

