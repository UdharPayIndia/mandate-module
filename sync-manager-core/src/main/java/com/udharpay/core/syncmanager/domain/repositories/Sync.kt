package com.udharpay.core.syncmanager.domain.repositories

import com.udharpay.core.syncmanager.domain.enities.*

interface Sync {
    fun dependencies(): List<String>
    fun priority(): SyncPriority
    fun existingSyncPolicy(): ExistingSyncPolicy
    fun syncFailurePolicy(): SyncFailurePolicy
    fun constraint(): SyncConstraint
    fun syncer(): Syncer
    fun syncRetryPolicy(): SyncRetryPolicy = SyncRetryPolicy.Builder().build()
}
