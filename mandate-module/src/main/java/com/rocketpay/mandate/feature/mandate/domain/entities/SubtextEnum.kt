package com.rocketpay.mandate.feature.mandate.domain.entities

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal sealed class SubtextEnum(val value: String, val stringResourceId: Int) {
    object AskCustomerToAcceptSubText : SubtextEnum("ask_customer_to_accept_subtext", R.string.rp_ask_customer_to_accept)
    object OneNewUpdateSubText : SubtextEnum("one_new_update_subtext", R.string.rp_one_new_update)
    object MandateAcceptedSubText : SubtextEnum("mandate_accepted_subtext", R.string.rp_mandate_accepted)
    object PaymentDueOnSubText : SubtextEnum("payment_due_on_subtext", R.string.rp_payment_due_on)
    object InstallmentCollectedSubText : SubtextEnum("installment_collected_subtext", R.string.rp_x_out_of_y_installment_completed)
    object OneInstallmentFailed : SubtextEnum("one_installment_failed", R.string.rp_one_installment_failed)
    object TwoNewUpdateSubText : SubtextEnum("two_new_update_subtext", R.string.rp_two_new_update)
    object MandatePausedSubText : SubtextEnum("mandate_paused_subtext", R.string.rp_mandate_paused)
    object MandateTerminatedSubText : SubtextEnum("mandate_terminated_subtext", R.string.rp_mandate_terminated)
    object MandateCancelledSubText : SubtextEnum("mandate_cancelled_subtext", R.string.rp_mandate_cancelled)
    object LinkExpiredCreateNewMandateSubText : SubtextEnum("link_expired_create_new_mandate_subtext", R.string.rp_link_expired_please_create_new_mandate)
    object RetryFailedInstallmentBefore: SubtextEnum("retry_failed_installment_before", R.string.rp_retry_failed_installment_before)
    companion object {
        val map by lazy {
            mapOf(
                "ask_customer_to_accept_subtext" to AskCustomerToAcceptSubText,
                "one_new_update_subtext" to OneNewUpdateSubText,
                "mandate_accepted_subtext" to MandateAcceptedSubText,
                "payment_due_on_subtext" to PaymentDueOnSubText,
                "installment_collected_subtext" to InstallmentCollectedSubText,
                "mandate_paused_subtext" to MandatePausedSubText,
                "mandate_terminated_subtext" to MandateTerminatedSubText,
                "two_new_update_subtext" to TwoNewUpdateSubText,
                "one_installment_failed" to OneInstallmentFailed,
                "mandate_cancelled_subtext" to MandateCancelledSubText,
                "link_expired_create_new_mandate_subtext" to LinkExpiredCreateNewMandateSubText,
                "retry_failed_installment_before" to RetryFailedInstallmentBefore
                )
        }

        fun get(value: String?): SubtextEnum? {
            return map[value]
        }

        fun getMandateSubTextTranslation(mandate: Mandate): String {
             return when{
                 mandate.subTextEnum == PaymentDueOnSubText -> {
                     val date = DateUtils.getDate(mandate.nextChargeAt, DateUtils.DAY_MONTH_DATE_FORMAT)
                     val amount = AmountUtils.format(mandate.getMandateInstallmentAmount())
                     ResourceManager.getInstance().getString(PaymentDueOnSubText.stringResourceId, amount, date)
                 }
                 mandate.subTextEnum == InstallmentCollectedSubText -> {
                     ResourceManager.getInstance().getString(InstallmentCollectedSubText.stringResourceId,
                         mandate.installmentsPaid, mandate.installments)
                 }
                 mandate.subTextEnum == RetryFailedInstallmentBefore -> {
                     val endDate = DateUtils.getDate(mandate.endAt, DateUtils.MONTH_DATE_FORMAT)
                     ResourceManager.getInstance().getString(RetryFailedInstallmentBefore.stringResourceId,
                         endDate)
                 }
                 mandate.subTextEnum != null -> {
                     ResourceManager.getInstance().getString(mandate.subTextEnum.stringResourceId)
                 }
                 else -> {
                     ""
                 }
            }
        }

    }
}
