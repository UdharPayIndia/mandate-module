package com.udharpay.core.syncmanager.domain.enities

sealed class SyncPriority(val value: Int) {
    object VeryHigh: SyncPriority(0)
    object High: SyncPriority(1)
    object Medium: SyncPriority(2)
    object Low: SyncPriority(3)
    object VeryLow: SyncPriority(4)

    companion object {
        val map by lazy {
            mapOf(
                0 to VeryHigh,
                1 to High,
                2 to Medium,
                3 to Low,
                4 to VeryLow
            )
        }
    }
}
