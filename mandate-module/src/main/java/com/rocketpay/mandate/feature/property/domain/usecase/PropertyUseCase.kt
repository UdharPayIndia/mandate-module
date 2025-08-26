package com.rocketpay.mandate.feature.property.domain.usecase

import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.entities.PropertyDom
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import kotlinx.coroutines.flow.Flow

internal class PropertyUseCase internal constructor(
    private val propertyRepository: PropertyRepository
) {

    fun getDirtyProperties(propertyType: PropertyType): List<PropertyDom>{
        return propertyRepository.getDirtyProperties(propertyType)
    }

    fun markPropertiesNonDirty(properties: List<PropertyDom>){
        propertyRepository.markPropertiesNonDirty(properties)
    }

    fun saveProperties(properties: List<PropertyDto>, propertyType: PropertyType){
        propertyRepository.saveProperties(properties, propertyType)
    }

    fun setProperty(key: String, value: String, propertyType: PropertyType) {
        propertyRepository.setProperty(key, value, propertyType)
    }

    fun setProperties(properties: Map<String, String?>, propertyType: PropertyType) {
        propertyRepository.setProperties(properties, propertyType)
    }

    fun getPropertyValue(key: String): String? {
        return propertyRepository.getPropertyValue(key)
    }

    fun getProperty(key: String): PropertyDom?{
        return propertyRepository.getProperty(key)
    }

    fun getPropertyLive(key: String): Flow<PropertyDom?> {
        return propertyRepository.getPropertyLive(key)
    }

    fun getMultipleLive(keys: List<String>): Flow<List<PropertyDom?>>{
        return propertyRepository.getMultipleLive(keys)
    }

    fun getProperties(propertyType: PropertyType): Map<String, String?> {
        return propertyRepository.getPropertiesByType(propertyType)
    }

}
