package com.rocketpay.mandate.feature.kyc.data.datasource.local

import com.rocketpay.mandate.common.basemodule.common.data.cache.DataStore

internal class KycDataStore(private val dataStore: DataStore) {

    companion object {
        const val KYC_DATA_STORE_NAME = "kyc"
        private const val IS_KYC_COMPLETED = "IS_KYC_COMPLETED"
        const val KYC_TYPE = "kyc_type"
    }

    fun setIsKycCompleted(flag: Boolean) {
        dataStore.setBoolean(IS_KYC_COMPLETED, flag)
    }

    fun isKycCompleted(): Boolean {
        return dataStore.getBoolean(IS_KYC_COMPLETED, false)
    }
}
