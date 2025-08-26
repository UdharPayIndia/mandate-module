package com.rocketpay.mandate.feature.kyc.data.mapper

import com.google.gson.reflect.TypeToken
import com.rocketpay.mandate.feature.kyc.data.entities.KycDto
import com.rocketpay.mandate.feature.kyc.data.entities.KycEntity
import com.rocketpay.mandate.feature.kyc.data.entities.KycWorkFlowDto
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class KycDtoToEntMapper : ListMapper<KycDto, KycEntity> {
    val type = object : TypeToken<List<KycWorkFlowDto>>() {}.type
    override fun map(source: KycDto): KycEntity {
        return KycEntity(
            id = source.id ?: "",
            state = source.status ?: "",
            workflow =  source.workflow.takeIf { workflow -> !workflow.isNullOrEmpty() }?.let { workflow ->
                JsonConverter.getInstance().toJson(workflow, type)
            } ?: ""
        )
    }
}
