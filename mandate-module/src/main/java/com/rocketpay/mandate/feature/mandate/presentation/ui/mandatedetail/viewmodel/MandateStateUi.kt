package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState

internal sealed class MandateStateUi(val value: MandateState, val text: Int, val color: Int, val background: Int) {
    object Pending : MandateStateUi(MandateState.Pending, R.string.rp_created, R.color.rp_yellow_2, R.color.rp_yellow_3)
    object UserAccepted : MandateStateUi(MandateState.Active, R.string.rp_active, R.color.rp_blue_2, R.color.rp_blue_5)
    object Active : MandateStateUi(MandateState.Active, R.string.rp_active, R.color.rp_blue_2, R.color.rp_blue_5)
    object Paused : MandateStateUi(MandateState.Paused, R.string.rp_paused, R.color.rp_orange_1, R.color.rp_orange_3)
    object Completed : MandateStateUi(MandateState.Completed, R.string.rp_finished, R.color.rp_green_2, R.color.rp_green_5)
    object Terminated : MandateStateUi(MandateState.Terminated, R.string.rp_terminated, R.color.rp_grey_3, R.color.rp_grey_4)
    object Cancelled : MandateStateUi(MandateState.Cancelled, R.string.rp_cancelled, R.color.rp_grey_3, R.color.rp_grey_4)
    object Expired : MandateStateUi(MandateState.Expired, R.string.rp_expired, R.color.rp_grey_3, R.color.rp_grey_4)

    companion object {
        val map by lazy {
            mapOf(
                MandateState.PartiallyCollected to Active,
                MandateState.Pending to Pending,
                MandateState.UserAccepted to UserAccepted,
                MandateState.Active to Active,
                MandateState.Completed to Completed,
                MandateState.Terminated to Terminated,
                MandateState.Cancelled to Cancelled,
                MandateState.Paused to Paused,
                MandateState.Expired to Expired
            )
        }

        fun getMandateStateUi(state: MandateState): MandateStateUi {
            return map[state] ?: Pending
        }
    }
}
