package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.data.mapper.TimeState
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.settlements.domain.usecase.PaymentOrderUseCase
import com.rocketpay.mandate.feature.settlements.presentation.ui.utils.SettlementUtils
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.ifNullOrEmpty
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.product.presentation.ui.utils.ProductUtils
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay

internal class InstallmentDetailStateMachine(
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase,
    private val propertyUseCase: PropertyUseCase,
    private val paymentOrderUseCase: PaymentOrderUseCase
) : SimpleStateMachineImpl<InstallmentDetailEvent, InstallmentDetailState, InstallmentDetailASF, InstallmentDetailUSF>(
    BaseAnalyticsHandler()
) {

    override fun startState(): InstallmentDetailState {
        return InstallmentDetailState()
    }

    override fun handleEvent(
        event: InstallmentDetailEvent,
        state: InstallmentDetailState
    ): Next<InstallmentDetailState?, InstallmentDetailASF?, InstallmentDetailUSF?> {
        return when (event) {
            is InstallmentDetailEvent.LoadMandateAndInstallment -> {
                if (event.installmentId == null || event.mandateId == null) {
                    noChange()
                } else {
                    next(state.copy(installmentId = event.installmentId,
                        mandateId = event.mandateId,
                        referenceId = event.referenceId
                    ),
                        InstallmentDetailASF.LoadMandate(event.mandateId))
                }
            }
            is InstallmentDetailEvent.FetchInstallment -> {
                if (!event.installmentId.isNullOrEmpty()) {
                    next(InstallmentDetailASF.FetchInstallment(event.installmentId))
                }else{
                    noChange()
                }
            }
            is InstallmentDetailEvent.MandateLoaded -> {
                next(state.copy(mandate = event.mandate),
                    InstallmentDetailASF.LoadInstallment(state.installmentId ?: ""))
            }
            is InstallmentDetailEvent.InstallmentLoaded -> {
                val totalCount = getJourneyCount(state, event)
                val journey = event.installment?.journey ?: emptyList()
                val sideEffect = if (state.isExpanded || event.installment?.state !is InstallmentState.SettlementSuccess) {
                        InstallmentDetailUSF.UpdatePayments(journey, true, totalCount,
                            state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                            event.installment?.dueDate ?: 0,
                            event.installment?.isMerchantCollected == true)
                    } else {
                        InstallmentDetailUSF.UpdatePayments(journey.filter { it.timeState is TimeState.Present },
                            false, totalCount,
                            state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                            event.installment?.dueDate ?: 0,
                            event.installment?.isMerchantCollected == true)
                    }
                next(state.copy(installment = event.installment),
                    InstallmentDetailASF.LoadPenaltyAndPaymentOrder(event.installment), sideEffect)
            }
            is InstallmentDetailEvent.RefreshClick -> {
                val installmentId = event.installmentId ?: state.installmentId
                if (installmentId == null) {
                    noChange()
                } else {
                    next(state.copy(isRefreshing = true,
                        installmentId = installmentId
                    ), InstallmentDetailASF.RefreshData(installmentId))
                }
            }
            is InstallmentDetailEvent.UnableToRefresh -> {
                next(state.copy(isRefreshing = false), InstallmentDetailUSF.ShowToast(event.message))
            }
            is InstallmentDetailEvent.DataRefreshed -> {
                next(state.copy(isRefreshing = false))
            }
            is InstallmentDetailEvent.InstallmentJourneyClick -> {
                val totalCount = getJourneyCount(state)
                if (state.installment?.journey.isNullOrEmpty()) {
                    noChange()
                } else {
                    val journey = state.installment?.journey ?: emptyList()
                    if (state.isExpanded) {
                        next(state.copy(isExpanded = false), InstallmentDetailUSF.UpdatePayments(
                            journey.filter { it.timeState is TimeState.Present }, false,
                            totalCount, state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                            state.installment?.dueDate ?: 0,
                            state.installment?.isMerchantCollected == true))
                    } else {
                        next(state.copy(isExpanded = true), InstallmentDetailUSF.UpdatePayments(journey, true,
                            totalCount, state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                            state.installment?.dueDate ?: 0,
                            state.installment?.isMerchantCollected == true))
                    }
                }
            }
            is InstallmentDetailEvent.TransactionIdCopyClick -> {
                next(InstallmentDetailUSF.Copy(event.message, event.link))
            }
            is InstallmentDetailEvent.RocketPayTransactionIdCopyClick -> {
                next(InstallmentDetailUSF.Copy(event.message, event.link))
            }
            is InstallmentDetailEvent.ContactUsClick -> {
                next(InstallmentDetailUSF.ContactUsClick)
            }

            InstallmentDetailEvent.RetryInstallmentClick -> {
                next(
                    InstallmentDetailUSF.OpenSelectRetryDateBottomSheet(
                        state.mandateId.orEmpty(),
                        state.installmentId.orEmpty()
                    )
                )
            }

            InstallmentDetailEvent.SkipInstallmentClick -> {
                next(
                    InstallmentDetailUSF.ShowSkipInstallmentConfirmation(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_delete_filled),
                        ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                        ResourceManager.getInstance().getString(R.string.rp_skip_installment_title),
                        if(!state.referenceId.isNullOrEmpty()){
                            ResourceManager.getInstance().getString(R.string.rp_skip_installment_detail_super_key)
                        }else{
                            ResourceManager.getInstance().getString(R.string.rp_skip_installment_detail)
                        },
                        ResourceManager.getInstance().getString(R.string.rp_skip_installment),
                        ResourceManager.getInstance().getString(R.string.rp_do_not_skip)
                    )
                )
            }
            InstallmentDetailEvent.SkipInstallmentConfirmed -> {
                if (state.installmentId == null || state.mandateId == null) {
                    noChange()
                } else {
                    next(
                        InstallmentDetailASF.SkipInstallment(state.mandateId, state.installmentId),
                        InstallmentDetailUSF.SkipInstallmentInProgress(
                            ResourceManager.getInstance().getString(R.string.rp_skipping_installment_title),
                            ResourceManager.getInstance().getString(R.string.rp_skipping_installment_detail),
                        )
                    )
                }
            }
            InstallmentDetailEvent.SkipInstallmentDismiss -> {
                next(InstallmentDetailUSF.DismissSkipInstallmentConfirmation)
            }
            InstallmentDetailEvent.InstallmentSkipped -> {
                next(InstallmentDetailUSF.InstallmentSkipped(
                    ResourceManager.getInstance().getString(R.string.rp_installment_skipped_title),
                    ResourceManager.getInstance().getString(R.string.rp_installment_skipped_detail)
                ))
            }
            is InstallmentDetailEvent.UnableToSkipInstallment -> {
                next(
                    InstallmentDetailUSF.UnableToSkipInstallment(
                        ResourceManager.getInstance().getString(R.string.rp_skip_installment_failed),
                        event.message
                    )
                )
            }
            is InstallmentDetailEvent.UpdatePenaltyDetails -> {
                next(state.copy(installmentPenalty = event.installmentPenalty))
            }
            is InstallmentDetailEvent.ChargePenaltyClick -> {
                next(
                    InstallmentDetailUSF.OpenEnterPenaltyBottomSheet(
                        state.mandateId.orEmpty(),
                        state.installmentId.orEmpty(),
                        state.installment?.amount ?: 0.0
                    )
                )
            }
            is InstallmentDetailEvent.MarkAsPaidClick -> {
                next(InstallmentDetailUSF.OpenInstallmentUpdateScreen(
                    state.installmentId ?: "",
                    state.installment?.paymentMode,
                    state.installment?.comments ?: ""))
            }
            is InstallmentDetailEvent.LoadSettlementBannerInfo -> {
                next(InstallmentDetailASF.LoadSettlementBannerInfo)
            }
            is InstallmentDetailEvent.UpdateSettlementBannerMessage -> {
                next(state.copy(settlementBannerMessage = event.message))
            }
            is InstallmentDetailEvent.ViewSettlementClick -> {
                next(InstallmentDetailUSF.OpenSettlementScreen(state.installment?.paymentOrderId.orEmpty()))
            }
            is InstallmentDetailEvent.SettlementBannerClick -> {
                next(InstallmentDetailUSF.OpenKyc)
            }
            is InstallmentDetailEvent.AddBankAccountClick -> {
                next(InstallmentDetailUSF.OpenBankAccount)
            }
            is InstallmentDetailEvent.UpdatePaymentOrder -> {
                next(state.copy(paymentOrder = event.paymentOrder), InstallmentDetailASF.LoadPenalty(state.installmentId.orEmpty()))
            }
            is InstallmentDetailEvent.RefreshInstallment -> {
                if(!state.installmentId.isNullOrEmpty()){
                    next(InstallmentDetailASF.RefreshInstallment(state.installmentId),
                        InstallmentDetailUSF.ShowLoader(ResourceManager.getInstance().getString(R.string.rp_refreshing_data)))
                }else{
                    noChange()
                }
            }
            is InstallmentDetailEvent.ActionFailed -> {
                next(InstallmentDetailUSF.ShowError(
                    ResourceManager.getInstance().getString(R.string.rp_action_failed),
                    event.errorMessage.ifNullOrEmpty(ResourceManager.getInstance().getString(R.string.rp_ivr_invoice_something_went_wrong))
                ))
            }
            is InstallmentDetailEvent.ActionSuccess -> {
                next(InstallmentDetailUSF.ShowToast(ResourceManager.getInstance().getString(R.string.rp_installment_refresh_success)))
            }
            is InstallmentDetailEvent.DismissLoader -> {
                next(InstallmentDetailUSF.DismissLoader)
            }
        }
    }

    private fun getJourneyCount(
        state: InstallmentDetailState,
        event: InstallmentDetailEvent.InstallmentLoaded? = null
    ): Int {
        val journey = (event?.installment ?: state.installment)?.journey?.sortedBy { it.createdAt }
        return journey?.size ?: 0
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: InstallmentDetailASF,
        dispatchEvent: (InstallmentDetailEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is InstallmentDetailASF.LoadMandate -> {
                mandateUseCase.getMandate(sideEffect.mandateId).collectIn(viewModelScope) {
                    if (it != null) {
                        dispatchEvent(InstallmentDetailEvent.MandateLoaded(mandate = it))
                    }
                }
            }
            is InstallmentDetailASF.FetchInstallment -> {
                installmentUseCase.fetchInstallment(sideEffect.installmentId)
                installmentUseCase.fetchInstallmentActions(sideEffect.installmentId)
                dispatchEvent(InstallmentDetailEvent.RefreshClick(installmentId = sideEffect.installmentId))
            }
            is InstallmentDetailASF.LoadInstallment -> {
                installmentUseCase.getInstallment(sideEffect.installmentId).collectIn(viewModelScope) {
                    if (it != null) {
                        dispatchEvent(InstallmentDetailEvent.InstallmentLoaded(installment = it))
                    }
                }
            }
            is InstallmentDetailASF.LoadPenaltyAndPaymentOrder -> {
                if(!sideEffect.installment?.id.isNullOrEmpty()) {
                    val paymentOrder = paymentOrderUseCase.getPaymentOrderById(sideEffect.installment?.paymentOrderId.orEmpty())
                    dispatchEvent(InstallmentDetailEvent.UpdatePaymentOrder(paymentOrder))
                }
            }
            is InstallmentDetailASF.RefreshData -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                delay(100)
                dispatchEvent(InstallmentDetailEvent.DataRefreshed)
            }
            is InstallmentDetailASF.SkipInstallment -> {
                when(val outcome = installmentUseCase.skipInstallment(sideEffect.mandateId, sideEffect.installmentId)) {
                    is Outcome.Error -> dispatchEvent(InstallmentDetailEvent.UnableToSkipInstallment(outcome.error.message.orEmpty()))
                    is Outcome.Success -> {
                        installmentUseCase.fetchInstallmentActions(sideEffect.installmentId)
                        dispatchEvent(InstallmentDetailEvent.InstallmentSkipped)
                    }
                }
            }
            is InstallmentDetailASF.LoadPenalty -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                when (val outcome = installmentUseCase.fetchInstallmentPenalty(sideEffect.installmentId)) {
                    is Outcome.Error -> {
                    }
                    is Outcome.Success -> {
                        dispatchEvent(InstallmentDetailEvent.UpdatePenaltyDetails(outcome.data))
                    }
                }
            }
            is InstallmentDetailASF.LoadSettlementBannerInfo -> {
                paymentOrderUseCase.fetchSettlementBannerInfo(propertyUseCase)
                propertyUseCase.getPropertyLive(ProductUtils.SETTLEMENT_BANNER_MESSAGE)
                    .collectIn(viewModelScope) {
                        dispatchEvent(InstallmentDetailEvent.UpdateSettlementBannerMessage(it?.value.orEmpty()))
                    }
            }
            is InstallmentDetailASF.RefreshInstallment -> {
                when (val outcome = installmentUseCase.refreshInstallment(sideEffect.installmentId)) {
                    is Outcome.Error -> {
                        dispatchEvent(InstallmentDetailEvent.ActionFailed(outcome.error.message.orEmpty()))
                    }
                    is Outcome.Success -> {
                        dispatchEvent(InstallmentDetailEvent.ActionSuccess)
                    }
                }
            }
        }
    }
}
