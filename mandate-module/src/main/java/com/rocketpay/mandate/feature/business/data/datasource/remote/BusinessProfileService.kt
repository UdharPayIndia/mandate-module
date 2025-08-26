package com.rocketpay.mandate.feature.business.data.datasource.remote

import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
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

}