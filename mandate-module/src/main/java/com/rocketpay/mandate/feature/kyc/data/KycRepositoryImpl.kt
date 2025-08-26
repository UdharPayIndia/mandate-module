package com.rocketpay.mandate.feature.kyc.data

import com.google.gson.JsonObject
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDao
import com.rocketpay.mandate.feature.kyc.data.datasource.local.KycDataStore
import com.rocketpay.mandate.feature.kyc.data.datasource.remote.KycService
import com.rocketpay.mandate.feature.kyc.data.entities.KycEntity
import com.rocketpay.mandate.feature.kyc.data.mapper.KycDtoToEntMapper
import com.rocketpay.mandate.feature.kyc.data.mapper.KycEntToDomMapper
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.repositories.KycRepository
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

internal class KycRepositoryImpl(
    private val kycDataStore: KycDataStore,
    private val kycDao: KycDao,
    private val kycService: KycService,
    private val kycDtoToEntMapper: KycDtoToEntMapper,
    private val kycEntToDomMapper: KycEntToDomMapper
): KycRepository {

    override fun getKyc(): Flow<Kyc?> {
        return kycDao.getAll().transform {
            if(it.isNullOrEmpty()){
                emit(null)
            }else{
                emit(kycEntToDomMapper.map(it[0]))
            }
        }
    }

    override suspend fun fetchKyc(propertyUseCase: PropertyUseCase): Outcome<Kyc> {
        return when(val outcome = kycService.fetchKycType()){
            is Outcome.Success -> {
                withContext(Dispatchers.IO){
                    propertyUseCase.setProperty(
                        KycDataStore.KYC_TYPE, outcome.data.type, PropertyType.Miscellaneous)
                }
               fetchKycDetails(outcome.data.type)
            }
            is Outcome.Error -> {
                outcome
            }
        }

    }

    private suspend fun fetchKycDetails(type: String): Outcome<Kyc> {
        return when(val outcome = kycService.fetchKyc(type)) {
            is Outcome.Success -> {
                val kycEntity = kycDtoToEntMapper.map(outcome.data)
                insertKyc(kycEntity)
                Outcome.Success(kycEntToDomMapper.map(kycEntity))
            }
            is Outcome.Error -> {
                outcome
            }
        }
    }

    override suspend fun submitKycItem(kycItem: KycWorkFlow, jsonObject: JsonObject): Outcome<Kyc> {
        return when(val outcome = kycService.submitKyc(kycItem, jsonObject)) {
            is Outcome.Success -> {
                val kycEntity = kycDtoToEntMapper.map(outcome.data)
                insertKyc(kycEntity)
                Outcome.Success(kycEntToDomMapper.map(kycEntity))
            }
            is Outcome.Error -> outcome
        }
    }

    override suspend fun initKycItem(kycItem: KycWorkFlow): Outcome<Kyc> {
        return when(val outcome = kycService.initKyc(kycItem)) {
            is Outcome.Success -> {
                val kycEntity = kycDtoToEntMapper.map(outcome.data)
                insertKyc(kycEntity)
                Outcome.Success(kycEntToDomMapper.map(kycEntity))
            }
            is Outcome.Error -> outcome
        }
    }

    private fun insertKyc(kycEntity: KycEntity) {
        val kycStatus = getKycStatusNonLive()
        setIsKycCompleted(kycStatus == KycStateEnum.Completed)
        kycDao.insertOne(kycEntity)
    }

    override fun getKycStatus(): Flow<KycStateEnum> {
        return kycDao.getAll().transform { kycEntities ->
            if (kycEntities.any { it.state == KycStateEnum.Completed.value }) {
                emit(KycStateEnum.Completed)
            } else if (kycEntities.any { it.state == KycStateEnum.UnderReview.value }) {
                emit(KycStateEnum.UnderReview)
            } else if (kycEntities.any { it.state == KycStateEnum.Rejected.value }) {
                emit(KycStateEnum.Rejected)
            } else {
                emit(KycStateEnum.Pending)
            }
        }
    }

    override fun getKycStatusNonLive(): KycStateEnum {
        val kycStatusEnum : KycStateEnum
        kycDao.getAllNonLive().let { kycEntities ->
            kycStatusEnum = if (kycEntities.any { it.state == KycStateEnum.Completed.value }) {
                KycStateEnum.Completed
            } else if (kycEntities.any { it.state == KycStateEnum.UnderReview.value }) {
                KycStateEnum.UnderReview
            } else if (kycEntities.any { it.state == KycStateEnum.Rejected.value }) {
                KycStateEnum.Rejected
            } else {
                KycStateEnum.Pending
            }
        }
        return kycStatusEnum
    }

    override fun setIsKycCompleted(flag: Boolean) {
        return kycDataStore.setIsKycCompleted(flag)
    }

    override fun isKycCompleted(): Boolean {
        return kycDataStore.isKycCompleted()
    }
}
