package com.rocketpay.mandate.feature.business.data.datasource.remote

import com.rocketpay.mandate.feature.business.data.datasource.entities.EnterprisePropertyDto
import com.rocketpay.mandate.feature.business.data.datasource.entities.EnterprisePropertyRequest
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class BusinessPropertyService {

    suspend fun pullBusinessProperties(): Outcome<List<PropertyDto>> {
        return NetworkRequestBuilder()
            .subUrl("/api/mas/properties/merchant-properties")
            .build()
            .get()
    }

    suspend fun pushBusinessProperties(businessPropertyDto: Map<String, String?>): Outcome<GenericErrorResponse> {
        return NetworkRequestBuilder()
            .subUrl("/api/mas/properties/merchant-properties")
            .body(businessPropertyDto)
            .build()
            .post()
    }

    suspend fun pullEnterprisePropertyList(accountId: String): Outcome<List<EnterprisePropertyDto>> {
        return NetworkRequestBuilder()
            .subUrl("/cus/properties/v1/bulk")
            .body(EnterprisePropertyRequest(
                listOf(accountId),
                listOf(
                    PropertyUtils.PENALTY_MINIMUM_AMOUNT,
                    PropertyUtils.PENALTY_MAXIMUM_AMOUNT,
                    PropertyUtils.IS_PENALTY_ENABLED,
                    PropertyUtils.IS_ADHOC_ENABLED,
                    PropertyUtils.TERMS_AND_CONDITION_URL,
                    PropertyUtils.PRIVACY_POLICY_URL,
                    PropertyUtils.MAX_UPI_NON_MONETISED_INSTALLMENT_AMOUNT,
                    PropertyUtils.BUSINESS_MOBILE_NUMBER)
            ))
            .build()
            .post()
    }
}