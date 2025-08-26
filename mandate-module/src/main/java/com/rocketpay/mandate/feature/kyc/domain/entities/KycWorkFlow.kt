package com.rocketpay.mandate.feature.kyc.domain.entities

import androidx.annotation.Keep
import com.google.gson.JsonObject
import com.google.gson.annotations.JsonAdapter
import com.rocketpay.mandate.common.basemodule.common.data.network.JsonToStringConverter

@Keep
internal data class KycWorkFlow(
    val id: String,
    val name: KycWorkFlowName,
    val priority: Int,
    val type: String,
    var status: KycItemState,
    val input: List<KycItemInputMeta>,
    val output: JsonObject,
    val error: KycItemErrorMeta?,
    var isExpanded: Boolean,
    var isPreviousStepPending: Boolean,
)

@Keep
internal data class KycItemInputMeta(
    val priority: Int,
    val type: String,
    val name: String,
    val meta: InputMeta?,
    //
    var value: String?,
)

@Keep
internal data class InputMeta(
    val title: String?,
    val sub_title: String?,
    //Multi Choice
    val options: List<String>?,
    //Document
    val allowed_extension: List<String>?,
    val size_limit: Int?,
    //Text
    val hint: String?,
    val regex: String?,
    val error: String?
)

@Keep
internal data class KycItemErrorMeta(
    val code: String,
    val message: String
)

@Keep
internal data class KycItemOwnerIdentityInputMeta(
    val inputs: HashMap<String, String>
)

@Keep
internal data class KycItemInitMetaHyperVergeDto(
    val type: String,
    @JsonAdapter(JsonToStringConverter::class)
    val meta: String
)

@Keep
internal data class KycItemInitMetaHyperVergeAndroid(
    val access_token: String,
    val transaction_id: String,
    val workflow: String
)

@Keep
internal data class KycItemInitMetaHyperVergeWeb(
    val url: String
)

@Keep
internal data class KycItemInitMetaHyperVerge(
    val type: String,
    val meta: Any
)


