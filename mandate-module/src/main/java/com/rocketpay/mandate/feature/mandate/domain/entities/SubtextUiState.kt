package com.rocketpay.mandate.feature.mandate.domain.entities

internal sealed class SubtextUiState(val value: String) {
    object Read : SubtextUiState("read")
    object Unread : SubtextUiState("unread")

    companion object {
        val map by lazy {
            mapOf(
                "read" to Read,
                "unread" to Unread,
            )
        }

        fun get(value: String?): SubtextUiState? {
            return map[value]
        }
    }
}