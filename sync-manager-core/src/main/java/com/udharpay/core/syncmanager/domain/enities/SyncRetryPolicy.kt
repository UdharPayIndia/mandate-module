package com.udharpay.core.syncmanager.domain.enities

class SyncRetryPolicy private constructor(val retryCount: Int, val backOffTime: Long, val syncBackOffPolicy: SyncBackOffPolicy) {

    class Builder {
        private var retryCount: Int = 0
        private var backOffTime: Long = 0L
        private var syncBackOffPolicy: SyncBackOffPolicy = SyncBackOffPolicy.Linear

        fun setRetryCount(retryCount: Int) = apply {
            this.retryCount = retryCount
        }

        fun setBackOffTime(backOffTime: Long) = apply {
            this.backOffTime = backOffTime
        }

        fun setBackOffPolicy(syncBackOffPolicy: SyncBackOffPolicy) = apply {
            this.syncBackOffPolicy = syncBackOffPolicy
        }

        fun build(): SyncRetryPolicy {
            return  SyncRetryPolicy(retryCount, backOffTime, syncBackOffPolicy)
        }
    }
}
