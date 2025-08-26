package com.rocketpay.mandate.feature.property.domain.repositories

import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.domain.entities.PropertyDom
import kotlinx.coroutines.flow.Flow

internal interface PropertyRepository {
    fun getDirtyProperties(propertyType: PropertyType): List<PropertyDom>
    fun markPropertiesNonDirty(properties: List<PropertyDom>)
    fun saveProperties(properties: List<PropertyDto>, propertyType: PropertyType)
    fun setProperty(key: String, value: String, propertyType: PropertyType)
    fun setProperties(properties: Map<String, String?>, propertyType: PropertyType)
    fun getPropertyValue(key: String): String?
    fun getProperty(key: String): PropertyDom?
    fun getPropertiesByType(propertyType: PropertyType): Map<String, String?>
    fun getPropertyLive(key: String): Flow<PropertyDom?>
    fun getMultipleLive(keys: List<String>): Flow<List<PropertyDom?>>
}
