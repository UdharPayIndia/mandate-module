package com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel

import com.rocketpay.mandate.R

internal sealed class InstallmentFrequency(val value: String, val suffix_ly: Int, val suffix_per_ly: Int, val suffix_s: Int) {
    object Daily : InstallmentFrequency("daily", R.string.rp_daily, R.string.rp_per_daily, R.string.rp_days)
    object Weekly : InstallmentFrequency("weekly", R.string.rp_weekly, R.string.rp_per_weekly, R.string.rp_weeks)
    object Monthly : InstallmentFrequency("monthly", R.string.rp_monthly, R.string.rp_per_monthly, R.string.rp_months)
    object Yearly : InstallmentFrequency("yearly", R.string.rp_yearly, R.string.rp_per_yearly, R.string.rp_years)
    object OneTimePayment : InstallmentFrequency("once", R.string.rp_one_time_payment, R.string.rp_per_one_time_payment, R.string.rp_single_payments)
    object Adhoc : InstallmentFrequency("adhoc", R.string.rp_custom_frequency_suffix_ly, R.string.rp_custom_frequency_suffix_per_ly, R.string.rp_custom_frequency_suffix_s)

    companion object {
        val map by lazy {
            mapOf(
                "daily" to Daily,
                "weekly" to Weekly,
                "monthly" to Monthly,
                "yearly" to Yearly,
                "once" to OneTimePayment,
                "adhoc" to Adhoc
            )
        }

        fun get(type: String): InstallmentFrequency {
            return map[type] ?: Monthly
        }
    }
}
