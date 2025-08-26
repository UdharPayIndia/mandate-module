package com.rocketpay.mandate.feature.kyc.domain.repositories

import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal interface KycRepository {
    fun getKyc(): Flow<Kyc?>
    suspend fun fetchKyc(propertyUseCase: PropertyUseCase): Outcome<Kyc>
    suspend fun submitKycItem(kycItem: KycWorkFlow, jsonObject: JsonObject): Outcome<Kyc>
    fun getKycStatus(): Flow<KycStateEnum>
    fun getKycStatusNonLive(): KycStateEnum
    suspend fun initKycItem(kycItem: KycWorkFlow): Outcome<Kyc>
    fun setIsKycCompleted(flag: Boolean)
    fun isKycCompleted(): Boolean
}
