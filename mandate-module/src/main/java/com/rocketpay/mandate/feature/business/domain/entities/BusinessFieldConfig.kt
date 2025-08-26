package com.rocketpay.mandate.feature.business.domain.entities

import androidx.annotation.Keep

@Keep
internal data class BusinessFieldConfig(
    val fields: List<BusinessField>
)

@Keep
internal data class BusinessInfoConfig(
    val fields: List<BusinessField>
)

@Keep
internal data class BusinessField(
    val title: String,
    val subtitle: String?,
    val hint: String?,
    val url: String?,
    val primary_text: String,
    val secondary_text: String?,

    val type: String,
    val type_values: List<BusinessFieldValue>,

    val priority: Int?,
    val default_type: String?,
    val type_meta: BusinessFieldValueMeta?
)

@Keep
internal data class BusinessFieldValue(
    val title: String,
    val subtitle: String?,
    val url: String?,

    val type: String,
    val value: String,
    val type_meta: BusinessFieldValueMeta?,

    val priority: Int
)

@Keep
internal data class BusinessFieldValueMeta(
    val hint: String,
    val regex: String,
    val error: String
)