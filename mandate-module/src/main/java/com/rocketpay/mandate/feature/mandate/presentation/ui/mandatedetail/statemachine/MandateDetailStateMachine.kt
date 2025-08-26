package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.usecase.InstallmentUseCase
import com.rocketpay.mandate.feature.kyc.domain.usecase.KycUseCase
import com.rocketpay.mandate.feature.mandate.data.MandateSyncer
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.usecase.MandateUseCase
import com.rocketpay.mandate.feature.mandate.presentation.ui.utils.WhatsAppMessageParserUtils
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.TimeConstant
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.common.domain.CommonUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class MandateDetailStateMachine(
    private val mandateUseCase: MandateUseCase,
    private val installmentUseCase: InstallmentUseCase,
    private val kycUseCase: KycUseCase,
    private val propertyUseCase: PropertyUseCase,
) : SimpleStateMachineImpl<MandateDetailEvent, MandateDetailState, MandateDetailASF, MandateDetailUSF>(
    BaseAnalyticsHandler()
) {

    var job: Job? = null
    override fun startState(): MandateDetailState {
        return MandateDetailState()
    }

    override fun handleEvent(
        event: MandateDetailEvent,
        state: MandateDetailState
    ): Next<MandateDetailState?, MandateDetailASF?, MandateDetailUSF?> {
        return when (event) {
            is MandateDetailEvent.Init -> {
                next(MandateDetailASF.InitConfig)
            }
            is MandateDetailEvent.LoadMandate -> {
                if (event.mandateId == null) {
                    noChange()
                } else {
                    next(state.copy(mandateId = event.mandateId, installmentSerialNumber = event.installmentSerialNumber),
                        MandateDetailASF.LoadMandate(event.mandateId))
                }
            }
            is MandateDetailEvent.LoadSuperKey -> {
                next(state.copy(superKeyId = event.superKeyId),
                    MandateDetailASF.LoadMandateByReferenceId(event.superKeyId),
                    MandateDetailUSF.ShowProgressDialog(
                        ResourceManager.getInstance().getString(R.string.rp_loading_mandate),
                        ResourceManager.getInstance().getString(R.string.rp_please_wait)
                    ))
            }
            is MandateDetailEvent.MandateLoaded -> {
                val mandateId = event.mandate?.id.orEmpty()
                val asyncTask = MandateDetailASF.LoadInstallments(mandateId = mandateId, true)
                val newState = setMandateAction(state.copy(mandate = event.mandate, mandateId = mandateId))
                if(event.mandate != null){
                    if (event.mandate?.state is MandateState.UserAccepted && shouldShowDateChangeDialog(event.mandate.startAt)) {
                        val sideEffect = MandateDetailUSF.ShowDateChangeDialog(
                            ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_warning_triangle),
                            ResourceManager.getInstance().getDrawable(R.color.rp_orange_1),
                            ResourceManager.getInstance().getString(R.string.rp_date_change_header),
                            ResourceManager.getInstance().getString(R.string.rp_date_change_detail),
                            ResourceManager.getInstance().getString(R.string.rp_ok),
                            ResourceManager.getInstance().getString(R.string.rp_chat_with_us)
                        )
                        next(newState.copy(paymentViewState = MandateDetailViewState.Empty), asyncTask, sideEffect)
                    } else if (event.mandate?.state is MandateState.Pending) {
                        next(newState.copy(paymentViewState = MandateDetailViewState.Empty),
                            MandateDetailASF.CheckPollAndLoadInstallments(mandateId = mandateId, event.mandate.state),
                            MandateDetailUSF.DismissProgressDialog)
                    } else if(event.mandate?.state is MandateState.PartiallyCollected && shouldShowRetryMessage(event.mandate.endAt)){
                        next(newState.copy(paymentViewState = MandateDetailViewState.Retry), asyncTask,
                            MandateDetailUSF.DismissProgressDialog)
                    }else{
                        next(newState.copy(paymentViewState = MandateDetailViewState.Filled), asyncTask,
                            MandateDetailUSF.DismissProgressDialog)
                    }
                }else{
                    next(newState.copy(paymentViewState = MandateDetailViewState.Empty),
                        MandateDetailUSF.ShowErrorDialog(
                            ResourceManager.getInstance().getString(R.string.rp_no_mandate_found)
                        ))
                }
            }
            is MandateDetailEvent.LoadInstallments -> {
                if(!event.mandateId.isNullOrEmpty()){
                    next(MandateDetailASF.LoadInstallments(event.mandateId, event.stopPolling))
                }else{
                    noChange()
                }
            }
            is MandateDetailEvent.InstallmentsLoaded -> {
                next(state.copy(installments = event.installments, installmentSerialNumber = -1),
                    MandateDetailUSF.UpdateInstallments(
                        installments = event.installments,
                        nextInstallmentId = event.nextInstallmentId,
                        showMandateTag = false,
                        manualMandate = state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                        installmentSerialNumber = state.installmentSerialNumber,
                        mandate = state.mandate
                    )
                )
            }
            is MandateDetailEvent.RefreshClick -> {
                val mandateId = event.mandateId ?: state.mandateId
                if (mandateId == null) {
                    noChange()
                } else {
                    next(state.copy(isRefreshing = true, mandateId = mandateId),
                        MandateDetailASF.RefreshData(mandateId, event.isAutomatically))
                }
            }
            is MandateDetailEvent.UnableToRefresh -> {
                next(state.copy(isRefreshing = false), MandateDetailUSF.ShowToast(event.message))
            }
            is MandateDetailEvent.DataRefreshed -> {
                if(!event.installments.isNullOrEmpty()){
                    next(state.copy(isRefreshing = false, installments = event.installments),
                        MandateDetailUSF.UpdateInstallments(
                            installments = event.installments,
                            nextInstallmentId = event.nextInstallmentId,
                            showMandateTag = false,
                            manualMandate = state.mandate?.paymentMethodDetail?.method == PaymentMethod.Manual,
                            mandate = state.mandate))
                }else{
                    next(state.copy(isRefreshing = false))
                }
            }
            MandateDetailEvent.CopyClick -> {
                if (state.mandate == null) {
                    noChange()
                } else {
                    next(MandateDetailUSF.Copy(state.mandate.mandateUrl))
                }
            }
            MandateDetailEvent.ShareOnWhatsAppClick -> {
                if (state.mandate == null) {
                    noChange()
                } else {
                    next(MandateDetailASF.ShareOnWhatsAppClick(state.mandate))
                }
            }
            is MandateDetailEvent.WhatsAppTemplateCreated -> {
                next(MandateDetailUSF.ShareOnWhatsApp(event.mandate.customerDetail.mobileNumber, event.message))
            }
            MandateDetailEvent.ResendRequestClick -> {
                if (state.mandateId == null) {
                    noChange()
                } else {
                    next(MandateDetailASF.ResendRequest(state.mandateId))
                }
            }
            is MandateDetailEvent.UnableToSendPaymentRequest -> {
                next(MandateDetailUSF.ShowToast(event.message))
            }
            is MandateDetailEvent.ShowErrorMessage -> {
                next(MandateDetailUSF.ShowToast(event.errorMessage))
            }
            MandateDetailEvent.PaymentRequestSent -> {
                next(MandateDetailUSF.ShowToast(ResourceManager.getInstance().getString(R.string.rp_resend_request_send)))
            }
            MandateDetailEvent.LoadMandateState -> {
                if (state.mandateId == null) {
                    noChange()
                } else {
                    next(MandateDetailASF.LoadMandateState(state.mandateId))
                }
            }
            is MandateDetailEvent.ChatWithUsClick -> {
                next(MandateDetailUSF.OpenChatWithUs)
            }
            is MandateDetailEvent.DateChangeDialogDismissClick -> {
                next(MandateDetailUSF.DismissDateChangeDialog)
            }
            is MandateDetailEvent.DeleteMandateClick -> {
                next(
                    MandateDetailUSF.ShowDeleteMandateDialog(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_delete_filled),
                        ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                        ResourceManager.getInstance().getString(R.string.rp_delete_mandate_title),
                        ResourceManager.getInstance().getString(R.string.rp_delete_mandate_detail),
                        ResourceManager.getInstance().getString(R.string.rp_delete),
                        ResourceManager.getInstance().getString(R.string.rp_cancel)
                    )
                )
            }
            is MandateDetailEvent.DeleteMandateConfirmClick -> {
                if (state.mandateId == null) {
                    noChange()
                } else {
                    next(
                        MandateDetailASF.DeleteMandate(state.mandateId),
                        MandateDetailUSF.DeleteMandateInProgress(
                            ResourceManager.getInstance().getString(R.string.rp_deleting_mandate_title),
                            ResourceManager.getInstance().getString(R.string.rp_deleting_mandate_detail),
                        )
                    )
                }
            }
            MandateDetailEvent.DeleteMandateDismissClick -> {
                next(MandateDetailUSF.DismissDeleteMandateDialog)
            }
            is MandateDetailEvent.MandateDeleted -> {
                next(MandateDetailUSF.GotoMandateList(ResourceManager.getInstance().getString(R.string.rp_delete_mandate_success)))
            }
            is MandateDetailEvent.UnableToDeleteMandate -> {
                next(
                    MandateDetailUSF.MandateDeletionFailed(
                        ResourceManager.getInstance().getString(R.string.rp_delete_mandate_failed),
                        event.message
                    )
                )
            }
            is MandateDetailEvent.CancelMandateClick -> {
                next(
                    MandateDetailUSF.ShowCancelMandateDialog(
                        ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_cross_round_filled),
                        ResourceManager.getInstance().getDrawable(R.color.rp_blue_2),
                        ResourceManager.getInstance().getString(R.string.rp_cancel_mandate_title),
                        ResourceManager.getInstance().getString(R.string.rp_cancel_mandate_detail),
                        ResourceManager.getInstance().getString(R.string.rp_cancel_mandate),
                        ResourceManager.getInstance().getString(R.string.rp_do_not_cancel)
                    )
                )
            }
            is MandateDetailEvent.CancelMandateConfirmClick -> {
                if (state.mandateId == null) {
                    noChange()
                } else {
                    next(
                        MandateDetailASF.CancelMandate(state.mandateId),
                        MandateDetailUSF.CancelMandateInProgress(
                            ResourceManager.getInstance().getString(R.string.rp_cancelling_mandate_title),
                            ResourceManager.getInstance().getString(R.string.rp_cancelling_mandate_detail),
                        )
                    )
                }
            }
            is MandateDetailEvent.MandateCancelled -> {
                next(MandateDetailUSF.DismissCancelMandateDialog)
            }
            is MandateDetailEvent.UnableToCancelMandate -> {
                next(
                    MandateDetailUSF.MandateCancelFailed(
                        ResourceManager.getInstance().getString(R.string.rp_cancel_mandate_failed),
                        event.message
                    )
                )
            }
            MandateDetailEvent.CancelMandateDismissClick -> {
                next(MandateDetailUSF.DismissCancelMandateDialog)
            }
            MandateDetailEvent.GotoMandateList -> {
                next(MandateDetailUSF.GotoMandateList(ResourceManager.getInstance().getString(R.string.rp_delete_mandate_success)))
            }
            is MandateDetailEvent.ItemClick -> {
                next(MandateDetailUSF.GotoInstallmentDetail(event.installment.id,
                    event.installment.mandateId, state.mandate?.referenceId))
            }
            is MandateDetailEvent.CreateNewInstallmentClick -> {
                if (state.mandate == null) {
                    noChange()
                } else {
                    next(MandateDetailUSF.GotoCreateNewInstallmentClick(state.mandate.id))
                }
            }
            is MandateDetailEvent.StartPolling -> {
                if(state.mandate?.state == MandateState.Pending){
                    next(MandateDetailASF.StartPolling(state.mandate.id))
                }else{
                    noChange()
                }
            }
            is MandateDetailEvent.StopPolling -> {
                next(MandateDetailASF.StopPolling)
            }
            is MandateDetailEvent.RefreshMandate -> {
                if(!state.mandate?.id.isNullOrEmpty()){
                    next(MandateDetailASF.RefreshMandate(state.mandate!!.id),
                        if(event.fromUser){
                            MandateDetailUSF.ShowToast(ResourceManager.getInstance().getString(R.string.rp_refreshing_data))
                        }else{
                            null
                        })
                }else{
                    noChange()
                }
            }
            is MandateDetailEvent.CallCustomerClick -> {
                next(MandateDetailUSF.CallNumber(state.mandate?.customerDetail?.mobileNumber.orEmpty()))
            }
            is MandateDetailEvent.UpdateDeviceDetailsCardState -> {
                next(state.copy(isOtherDetailsExpanded = !state.isOtherDetailsExpanded))
            }
            is MandateDetailEvent.CloseScreen -> {
                next(MandateDetailUSF.CloseScreen)
            }
        }
    }

    private fun setMandateAction(state: MandateDetailState): MandateDetailState {
        if(state.mandate?.paymentMethodDetail?.method != PaymentMethod.Manual){
            state.deleteEnable = state.mandate?.state in arrayOf(MandateState.Pending, MandateState.Expired)
            state.cancelEnable = state.mandate?.state in arrayOf(MandateState.Active, MandateState.Paused, MandateState.UserAccepted, MandateState.PartiallyCollected)
        }else{
            state.deleteEnable = true
        }
        return state
    }

    private fun shouldShowDateChangeDialog(startDate: Long): Boolean {
        return (startDate + TimeConstant.DAY) <= System.currentTimeMillis()
    }

    private fun shouldShowRetryMessage(endDate: Long): Boolean {
        return endDate > System.currentTimeMillis()
    }


    override suspend fun handleAsyncSideEffect(
        sideEffect: MandateDetailASF,
        dispatchEvent: (MandateDetailEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            is MandateDetailASF.InitConfig -> {
                SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
            }
            is MandateDetailASF.LoadMandate -> {
                mandateUseCase.getMandate(sideEffect.mandateId).collectIn(viewModelScope) { mandate ->
                    mandate?.let {
                        withContext(Dispatchers.IO) {
                            mandateUseCase.markMandateSubtextRead(it)
                        }
                        dispatchEvent(MandateDetailEvent.MandateLoaded(it))
                    }
                }
            }
            is MandateDetailASF.LoadMandateByReferenceId -> {
                mandateUseCase.getMandateByReferenceId(sideEffect.referenceId).collectIn(viewModelScope) { mandate ->
                    mandate?.let {
                        withContext(Dispatchers.IO) {
                            mandateUseCase.markMandateSubtextRead(it)
                        }
                    }
                    dispatchEvent(MandateDetailEvent.MandateLoaded(mandate))
                }
            }
            is MandateDetailASF.CheckPollAndLoadInstallments -> {
                if(sideEffect.mandateState != MandateState.Pending){
                    dispatchEvent(MandateDetailEvent.StartPolling)
                }else{
                    dispatchEvent(MandateDetailEvent.StartPolling)
                }
                dispatchEvent(MandateDetailEvent.LoadInstallments(sideEffect.mandateId, sideEffect.mandateState != MandateState.Pending))
            }
            is MandateDetailASF.LoadInstallments -> {
                if(sideEffect.stopPolling){
                    dispatchEvent(MandateDetailEvent.StartPolling)
                }
                if(!sideEffect.mandateId.isNullOrEmpty()){
                    installmentUseCase.getInstallments(sideEffect.mandateId).collectIn(viewModelScope) {
                        val installment = getNextInstallment(it)
                        dispatchEvent(MandateDetailEvent.InstallmentsLoaded(it, installment?.id))
                    }
                }
            }
            is MandateDetailASF.RefreshData -> {
                if(sideEffect.isAutomatically){
                    val installment = installmentUseCase.getInstallmentsNonLive(sideEffect.mandateId)
                    val nextInstallment = getNextInstallment(installment)
                    dispatchEvent(MandateDetailEvent.DataRefreshed(installment, nextInstallment?.id))
                }else{
                    SyncManager.getInstance().enqueue(MandateSyncer.TYPE)
                    delay(100)
                    dispatchEvent(MandateDetailEvent.DataRefreshed())
                }
            }
            is MandateDetailASF.ResendRequest -> {
                when (val outcome = mandateUseCase.sendPaymentRequest(sideEffect.mandateId)) {
                    is Outcome.Error -> dispatchEvent(MandateDetailEvent.UnableToSendPaymentRequest(outcome.error.message.orEmpty()))
                    is Outcome.Success -> dispatchEvent(MandateDetailEvent.PaymentRequestSent)
                }
            }
            is MandateDetailASF.LoadMandateState -> {
                viewModelScope.launch(Dispatchers.IO) {
                    when (mandateUseCase.fetchMandateState(sideEffect.mandateId)) {
                        is Outcome.Error -> {
                            // No action required
                        }
                        is Outcome.Success -> {
                            // No action required
                        }
                    }
                }
            }
            is MandateDetailASF.DeleteMandate -> {
                when (val outcome = mandateUseCase.deleteMandate(sideEffect.mandateId)) {
                    is Outcome.Error -> {
                        dispatchEvent(MandateDetailEvent.UnableToDeleteMandate(outcome.error.message.orEmpty()))
                    }
                    is Outcome.Success -> {
                        dispatchEvent(MandateDetailEvent.MandateDeleted)
                    }
                }
            }
            is MandateDetailASF.CancelMandate -> {
                when (val outcome = mandateUseCase.cancelMandate(sideEffect.mandateId)) {
                    is Outcome.Error -> {
                        dispatchEvent(MandateDetailEvent.UnableToCancelMandate(outcome.error.message.orEmpty()))
                    }
                    is Outcome.Success -> {
                        dispatchEvent(MandateDetailEvent.MandateCancelled)
                    }
                }
            }
            is MandateDetailASF.ShareOnWhatsAppClick -> {
                val whatsAppMessageConfig = mandateUseCase.getWhatsAppMessageConfig()
                val messageTemplate = WhatsAppMessageParserUtils.getMessageForSharePaymentLink(whatsAppMessageConfig,
                    sideEffect.mandate, CommonUseCase.getInstance().getName())
                dispatchEvent(MandateDetailEvent.WhatsAppTemplateCreated(sideEffect.mandate, whatsAppMessageConfig.experiment, messageTemplate))
            }
            is MandateDetailASF.StartPolling -> {
                if(job == null) {
                    job = viewModelScope.launch {
                        channelFlow {
                            while (!isClosedForSend) {
                                delay(5000)
                                send(dispatchEvent(MandateDetailEvent.RefreshMandate()))
                            }
                        }.flowOn(Dispatchers.IO).collect{

                        }
                    }
                }
            }
            is MandateDetailASF.StopPolling -> {
                job?.cancel()
                job = null
            }
            is MandateDetailASF.RefreshMandate -> {
                mandateUseCase.refreshMandate(sideEffect.mandateId)
            }
        }
    }

    private fun getNextInstallment(installments: List<Installment>): Installment? {
        return if (installments.isEmpty()) {
            null
        } else {
            val tempInstallmentMap = installments.map {
                Pair(it, (it.dueDate - System.currentTimeMillis()) / TimeConstant.DAY)
            }.filter { it.second >= 0 }
            if (tempInstallmentMap.isEmpty()) {
                null
            } else {
                tempInstallmentMap.first().first
            }
        }
    }
}
