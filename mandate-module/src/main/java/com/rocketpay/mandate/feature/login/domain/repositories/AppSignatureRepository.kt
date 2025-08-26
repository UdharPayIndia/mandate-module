package com.rocketpay.mandate.feature.login.domain.repositories

internal interface AppSignatureRepository {
    fun getAppSignature(): String
}
