package com.rocketpay.mandate.feature.business.domain.repositories

import com.rocketpay.mandate.feature.business.data.datasource.entities.EnterprisePropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal interface BusinessPropertyRepository {
    suspend fun pullBusinessProperties(): Outcome<List<PropertyDto>>

    suspend fun pushBusinessProperties(businessProperty: Map<String, String?>): Outcome<GenericErrorResponse>
    suspend fun pullEnterprisePropertyList(propertyRepository: PropertyRepository): Outcome<List<EnterprisePropertyDto>>
}