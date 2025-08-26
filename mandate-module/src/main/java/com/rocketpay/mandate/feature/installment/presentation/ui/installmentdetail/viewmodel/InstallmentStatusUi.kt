package com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState

internal sealed class InstallmentStateUi(val text: Int, val icon: Int, val color: Int, val background: Int) {
    object Created : InstallmentStateUi(R.string.rp_installment_created, R.drawable.rp_ic_clock, R.color.rp_yellow_1, R.color.rp_yellow_2)
    object Outstanding: InstallmentStateUi(R.string.rp_outstanding, R.drawable.rp_ic_warning_round, R.color.rp_red_2, R.color.rp_red_2)

    object CollectionInitiated : InstallmentStateUi(R.string.rp_collection_initiated, R.drawable.rp_ic_arrow_round, R.color.rp_yellow_2, R.color.rp_yellow_2)
    object CollectionFailed : InstallmentStateUi(R.string.rp_collection_failed, R.drawable.rp_ic_warning_round, R.color.rp_yellow_2, R.color.rp_yellow_2)
    object CollectionSkipped : InstallmentStateUi(R.string.rp_skipped, R.drawable.rp_ic_skip, R.color.rp_red_2, R.color.rp_red_2)

    object CollectionSuccess : InstallmentStateUi(R.string.rp_collection_success, R.drawable.rp_ic_success, R.color.rp_blue_2, R.color.rp_blue_2)

    object SettlementInitiated : InstallmentStateUi(R.string.rp_settlement_initiated, R.drawable.rp_ic_arrow_round, R.color.rp_blue_2, R.color.rp_blue_2)
    object SettlementFailed : InstallmentStateUi(R.string.rp_settlement_failed, R.drawable.rp_ic_warning_round, R.color.rp_blue_2, R.color.rp_blue_2)
    object Settled : InstallmentStateUi(R.string.rp_settlement_success, R.drawable.rp_ic_success, R.color.rp_green_2, R.color.rp_green_2)

    object RefundInitiated : InstallmentStateUi(R.string.rp_refund_to_customer_initiated, R.drawable.rp_ic_cash_refund, R.color.rp_orange_1, R.color.rp_orange_1)
    object RefundFailed : InstallmentStateUi(R.string.rp_refund_failed, R.drawable.rp_ic_warning_round, R.color.rp_red_2, R.color.rp_red_2)
    object RefundSuccess : InstallmentStateUi(R.string.rp_refunded_to_customer, R.drawable.rp_ic_cash_refund, R.color.rp_orange_1, R.color.rp_orange_1)

    object Terminated : InstallmentStateUi(R.string.rp_installment_terminated, R.drawable.rp_ic_cross_round_filled, R.color.rp_red_2, R.color.rp_red_2)
    object ManuallyCollected : InstallmentStateUi(R.string.rp_manually_collected, R.drawable.rp_ic_success, R.color.rp_green_2, R.color.rp_green_2)


    companion object {
        val map by lazy {
            mapOf(
                InstallmentState.Created to Created,
                InstallmentState.Terminated to Terminated,

                InstallmentState.CollectionInitiated to CollectionInitiated,
                InstallmentState.CollectionSuccess to CollectionSuccess,
                InstallmentState.CollectionFailed to CollectionFailed,

                InstallmentState.SettlementInitiated to SettlementInitiated,
                InstallmentState.SettlementSuccess to Settled,
                InstallmentState.SettlementFailed to SettlementFailed,

                InstallmentState.RefundInitiated to RefundInitiated,
                InstallmentState.RefundSuccess to RefundSuccess,
                InstallmentState.RefundFailed to RefundFailed,

                InstallmentState.Skipped to CollectionSkipped,
                InstallmentState.Scheduled to CollectionInitiated
            )
        }

        val manualMap by lazy {
            mapOf(
                InstallmentState.Created to Created,
                InstallmentState.Skipped to ManuallyCollected,
                InstallmentState.Terminated to ManuallyCollected
            )
        }

        fun get(state: InstallmentState?, isManual: Boolean): InstallmentStateUi {
            return if(isManual){
                manualMap[state] ?: Created
            }else{
                map[state] ?: Created
            }
        }

    }
}
