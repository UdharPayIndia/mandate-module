package com.rocketpay.mandate.feature.kyc.domain.usecase

import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.repositories.KycRepository
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator
import kotlinx.coroutines.flow.Flow

internal class KycUseCase internal constructor(
    private val kycRepository: KycRepository,
    private val dataValidator: DataValidator
) {
    internal fun getKyc(): Flow<Kyc?> {
        return kycRepository.getKyc()
    }

    internal suspend fun fetchKyc(propertyUseCase: PropertyUseCase): Outcome<Kyc>{
        return kycRepository.fetchKyc(propertyUseCase)
    }

    internal suspend fun submitKycItem(kycItem: KycWorkFlow, jsonObject: JsonObject): Outcome<Kyc> {
        return kycRepository.submitKycItem(kycItem, jsonObject)
    }

    internal suspend fun initKycItem(kycItem: KycWorkFlow): Outcome<Kyc> {
        return kycRepository.initKycItem(kycItem)
    }

    internal fun getKycStatus(): Flow<KycStateEnum> {
        return kycRepository.getKycStatus()
    }

    internal suspend fun getKycStatusNonLive(): KycStateEnum{
        return kycRepository.getKycStatusNonLive()
    }

    fun setIsKycCompleted(flag: Boolean) {
        return kycRepository.setIsKycCompleted(flag)
    }

    fun isKycCompleted(): Boolean {
        return kycRepository.isKycCompleted()
    }

}
