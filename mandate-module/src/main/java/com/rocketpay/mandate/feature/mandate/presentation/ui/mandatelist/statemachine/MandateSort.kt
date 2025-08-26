package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine

import com.rocketpay.mandate.R


internal sealed class MandateSort(val value: String, val text: Int) {
    object Newest : MandateSort("newest", R.string.rp_sort_newest)
    object LatestUpdate : MandateSort("latest_update", R.string.rp_sort_latest_update)
    object HighestAmount : MandateSort("highest_amount", R.string.rp_sort_highest_amount)
    object Oldest : MandateSort("oldest", R.string.rp_sort_oldest)
    object NextInstallment : MandateSort("next_installment", R.string.rp_sort_next_installment)

    companion object {
        fun get(sortType: String): MandateSort {
            return map[sortType] ?: LatestUpdate
        }

        val map by lazy {
            mapOf(
                "latest_update" to LatestUpdate,
                "newest" to Newest,
                "highest_amount" to HighestAmount,
                "oldest" to Oldest,
                "next_installment" to NextInstallment
            )
        }
    }
}
