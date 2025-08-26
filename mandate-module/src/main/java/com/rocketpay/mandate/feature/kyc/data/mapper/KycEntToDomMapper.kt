package com.rocketpay.mandate.feature.kyc.data.mapper

import com.google.gson.reflect.TypeToken
import com.rocketpay.mandate.feature.kyc.data.entities.KycEntity
import com.rocketpay.mandate.feature.kyc.data.entities.KycWorkFlowDto
import com.rocketpay.mandate.feature.kyc.domain.entities.Kyc
import com.rocketpay.mandate.feature.kyc.domain.entities.KycStateEnum
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class KycEntToDomMapper(private val kycItemDtoToDomMapper: KycItemDtoToDomMapper) : ListMapper<KycEntity, Kyc> {
    override fun map(source: KycEntity): Kyc {
        return Kyc(
            id = source.id,
            state = KycStateEnum.get(source.state),
            workflow = getKycItems(source.workflow, kycItemDtoToDomMapper)
        )
    }

    private fun getKycItems(kycItems: String, kycItemDtoToDomMapper: KycItemDtoToDomMapper): List<KycWorkFlow> {
        return if (kycItems.isEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<KycWorkFlowDto>>(){}.type
            val kycWorkFlowDto = JsonConverter.getInstance().fromJson<List<KycWorkFlowDto>>(kycItems, type)
            if (kycWorkFlowDto == null) {
                emptyList()
            } else {
                kycItemDtoToDomMapper.mapList(kycWorkFlowDto)
            }
        }
    }
}
