package com.udharpay.core.syncmanager.domain.usecases

import com.udharpay.core.syncmanager.domain.enities.ExistingSyncPolicy
import com.udharpay.core.syncmanager.domain.enities.SyncRequest
import com.udharpay.core.syncmanager.domain.enities.SyncStatus

class ExistingSyncPolicyHandler {

    fun isEligibleRequest(existingRequest: SyncRequest, existingSyncPolicy: ExistingSyncPolicy): Boolean {
        return when (existingRequest.syncStatus) {
            SyncStatus.Enqueue -> {
                false // Ignore new request
            }
            SyncStatus.Success -> {
                true
            }
            SyncStatus.Failed -> {
                true
            }
            SyncStatus.InProgress -> {
                when (existingSyncPolicy) {
                    ExistingSyncPolicy.Append -> {
                        true
                    }
                    ExistingSyncPolicy.Keep -> {
                        false // Ignore new request
                    }
                }
            }
        }
    }
}
