package com.rocketpay.mandate.feature.installment.presentation.ui.penalty.statemachine

import android.graphics.drawable.Drawable
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentPenalty
import com.rocketpay.mandate.main.init.MandateManager
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class EnterPenaltyAmountState(
    val mandateId: String = "",
    val installmentId: String = "",
    var installmentAmount: String = "",
    val penaltyAmount: String = "",
    val penaltyAmountError: String = "",
    val isEnabled: Boolean = false,
    val minimumAmount: Double = MandateManager.getInstance().penaltyMinimumAmount
) : BaseState(EnterPenaltyAmountScreen)

internal sealed class EnterPenaltyAmountEvent(name: String? = null) : BaseEvent(name) {
    data class Init(
        val mandateId: String,
        val installmentId: String,
        val installmentAmount: String
    ): EnterPenaltyAmountEvent()
    data class UpdatePenaltyAmount(
        val penaltyAmount: String
    ): EnterPenaltyAmountEvent()
    data object SubmitPenalty: EnterPenaltyAmountEvent()
    data object ChargePenaltyConfirmed: EnterPenaltyAmountEvent("bounce_penalty_popup_confirm")
    data object ChargePenaltyDismiss: EnterPenaltyAmountEvent("bounce_penalty_popup_dismiss")
    data object CloseProgressDialog: EnterPenaltyAmountEvent()
    data class ChargePenaltyFailed(
        val errorCode: String,
        val errorMessage: String
    ): EnterPenaltyAmountEvent()
    data object ChargePenaltySucceed: EnterPenaltyAmountEvent()
    data class UpdatePenaltyDetails(val data: InstallmentPenalty) : EnterPenaltyAmountEvent()
}


internal sealed class EnterPenaltyAmountASF : AsyncSideEffect {
    data class ChargePenalty(
        val mandateId: String,
        val installmentId: String,
        val installmentAmount: String
    ): EnterPenaltyAmountASF()
    data class LoadPenalty(
        val installmentId: String
    ): EnterPenaltyAmountASF()
}


internal sealed class EnterPenaltyAmountUSF : UiSideEffect {
    data class ShowPenaltyConfirmation(
        val headerDrawable: Drawable,
        val headerBackground: Drawable,
        val title: String,
        val detail: String,
        val actionText: String,
        val secondaryBtnText: String
    ): EnterPenaltyAmountUSF()
    data class ShowProgressDialog(
        val title: String,
        val detail: String
    ): EnterPenaltyAmountUSF()
    data class ShowErrorDialog(
        val title: String,
        val detail: String
    ): EnterPenaltyAmountUSF()
    data object DismissProgressDialog: EnterPenaltyAmountUSF()
    data object DismissConfirmDialog: EnterPenaltyAmountUSF()
    data object CloseScreen: EnterPenaltyAmountUSF()
}

internal object EnterPenaltyAmountScreen : Screen("enter_penalty_amount")
