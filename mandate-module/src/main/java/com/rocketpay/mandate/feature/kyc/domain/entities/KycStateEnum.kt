package com.rocketpay.mandate.feature.kyc.domain.entities

internal sealed class KycStateEnum(val value: String) {
    object Initiated : KycStateEnum("initiated")
    object Pending : KycStateEnum("in_progress")
    object Completed : KycStateEnum("success")
    object UnderReview : KycStateEnum("review")
    object Rejected : KycStateEnum("failed")

    companion object {
        val map by lazy {
            mapOf(
                "initiated" to Initiated,
                "in_progress" to Pending,
                "success" to Completed,
                "review" to UnderReview,
                "failed" to Rejected
            )
        }

        fun get(value: String): KycStateEnum {
            return map[value] ?: Initiated
        }

        fun isInReviewOrCompleted(kycState: String?): Boolean {
            return kycState in listOf(UnderReview.value, Completed.value)
        }
    }
}
