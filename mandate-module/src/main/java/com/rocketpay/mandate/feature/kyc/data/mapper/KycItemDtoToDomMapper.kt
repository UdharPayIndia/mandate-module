package com.rocketpay.mandate.feature.kyc.data.mapper

import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.rocketpay.mandate.feature.kyc.data.entities.KycWorkFlowDto
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemInputMeta
import com.rocketpay.mandate.feature.kyc.domain.entities.KycItemState
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlow
import com.rocketpay.mandate.feature.kyc.domain.entities.KycWorkFlowName
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class KycItemDtoToDomMapper : ListMapper<KycWorkFlowDto, KycWorkFlow> {
    val type = object : TypeToken<List<KycItemInputMeta>>() {}.type
    override fun map(source: KycWorkFlowDto): KycWorkFlow {
        return KycWorkFlow(
            id = source.id,
            name = KycWorkFlowName.get(source.name),
            type = source.type,
            status = KycItemState.get(source.status),
            priority = source.priority,
            input = source.input ?: emptyList(),
            output = source.output ?: JsonObject(),
            error = source.error,
            isExpanded = false,
            isPreviousStepPending = false
        )
    }
}
