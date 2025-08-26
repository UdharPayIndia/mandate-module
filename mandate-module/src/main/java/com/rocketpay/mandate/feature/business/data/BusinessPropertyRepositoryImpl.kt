package com.rocketpay.mandate.feature.business.data

import com.rocketpay.mandate.feature.business.data.datasource.remote.BusinessPropertyService
import com.rocketpay.mandate.feature.business.domain.repositories.BusinessPropertyRepository
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class BusinessPropertyRepositoryImpl(
    private val businessPropertyService: BusinessPropertyService
): BusinessPropertyRepository {

    override suspend fun pullBusinessProperties(): Outcome<List<PropertyDto>> {
        return businessPropertyService.pullBusinessProperties()
    }

    override suspend fun pushBusinessProperties(businessProperty: Map<String, String?>): Outcome<GenericErrorResponse> {
        return businessPropertyService.pushBusinessProperties(businessProperty)
    }

}