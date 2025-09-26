package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine

import com.rocketpay.mandate.BuildConfig
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import com.rocketpay.mandate.main.init.MandateManager
import kotlinx.coroutines.CoroutineScope
import kotlin.text.format

internal class EnterPenaltyAmountStateMachine(
    private val installmentUseCase: InstallmentUseCase,
    private val propertyUseCase: PropertyUseCase
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
                        installmentAmount = event.installmentAmount,
                        customerName = event.customerName
                    )
                )
            }
            is EnterPenaltyAmountEvent.UpdatePenaltyAmount -> {
                val penaltyAmount = AmountUtils.stringToDouble(event.penaltyAmount)
                val installmentAmount = AmountUtils.stringToDouble(state.installmentAmount)
                val maximumAmount = maxOf(installmentAmount, state.maximumAmount)
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
                }else if(penaltyAmount > maximumAmount){
                    next(state.copy(penaltyAmount = event.penaltyAmount,
                        penaltyAmountError = ResourceManager.getInstance().getString(R.string.rp_penalty_amount_should_be_lesser_than_installment_amount,
                            AmountUtils.format(maximumAmount)),
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
                        ResourceManager.getInstance().getString(R.string.rp_confirm_penalty_charge),
                        ResourceManager.getInstance().getString(
                            R.string.rp_charge_amount_as_bounce_penalty_from_customer,
                            AmountUtils.format(AmountUtils.stringToDouble(state.penaltyAmount)),
                            state.customerName
                        ),
                        ResourceManager.getInstance().getString(R.string.rp_charge_penalty),
                        ResourceManager.getInstance().getString(R.string.rp_cancel)
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
            is EnterPenaltyAmountEvent.LoadConfig -> {
                next(EnterPenaltyAmountASF.LoadConfig)
            }
            is EnterPenaltyAmountEvent.ConfigLoaded -> {
                next(state.copy(
                    maximumAmount = event.maximumPenaltyAmount,
                    minimumAmount = event.minimumPenaltyAmount)
                )
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
            is EnterPenaltyAmountASF.LoadConfig -> {
                propertyUseCase.getMultiplePropertyLive(
                    listOf(PropertyUtils.PENALTY_MAXIMUM_AMOUNT,
                    PropertyUtils.PENALTY_MINIMUM_AMOUNT)
                ).collectIn(viewModelScope){
                    val minimumPenaltyAmount = it.find { it?.id == PropertyUtils.PENALTY_MINIMUM_AMOUNT }?.value.double()
                    val maximumPenaltyAmount = it.find { it?.id == PropertyUtils.PENALTY_MAXIMUM_AMOUNT }?.value.double()
                    dispatchEvent(EnterPenaltyAmountEvent.ConfigLoaded(minimumPenaltyAmount, maximumPenaltyAmount))
                }
            }
        }
    }

}

