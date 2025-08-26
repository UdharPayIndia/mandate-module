package com.rocketpay.mandate.feature.kyc.domain.entities

internal sealed class KycItemState(val value: String) {
    object Initiated : KycItemState("initiated")
    object Pending : KycItemState("in_progress")
    object Completed : KycItemState("success")
    object UnderReview : KycItemState("review")
    object Rejected : KycItemState("failed")

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

        fun get(value: String?): KycItemState {
            return map[value] ?: Initiated
        }
    }
}
