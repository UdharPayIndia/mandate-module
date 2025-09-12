package com.rocketpay.mandate.feature.business.data

import com.rocketpay.mandate.feature.business.data.datasource.entities.EnterprisePropertyDto
import com.rocketpay.mandate.feature.business.data.datasource.remote.BusinessPropertyService
import com.rocketpay.mandate.feature.business.domain.repositories.BusinessPropertyRepository
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import com.rocketpay.mandate.main.init.MandateManager
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

    override suspend fun pullEnterprisePropertyList(propertyRepository: PropertyRepository): Outcome<List<EnterprisePropertyDto>>{
        val outcome = businessPropertyService.pullEnterprisePropertyList(MandateManager.getInstance().getEnterpriseId())
        if(outcome is Outcome.Success){
            outcome.data.forEach { data ->
                val keys = data.properties.keys
                keys?.forEach {
                    propertyRepository.saveProperty(
                        PropertyDto(it, data.properties.get(it)?.orEmpty()),
                        PropertyType.Enterprise)
                }
            }
        }
    }
}
