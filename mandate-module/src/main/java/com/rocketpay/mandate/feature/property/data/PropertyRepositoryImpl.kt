package com.rocketpay.mandate.feature.property.data

import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.datasource.local.PropertyDao
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import com.rocketpay.mandate.feature.property.data.mapper.PropertyDomToEntMapper
import com.rocketpay.mandate.feature.property.data.mapper.PropertyEntToDomMapper
import com.rocketpay.mandate.feature.property.domain.entities.PropertyDom
import com.rocketpay.mandate.feature.property.domain.repositories.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal class PropertyRepositoryImpl(
    private val propertyDao: PropertyDao,
    private val propertyEntToDomMapper: PropertyEntToDomMapper,
    private val propertyDomToEntMapper: PropertyDomToEntMapper
): PropertyRepository {

    override fun getDirtyProperties(propertyType: PropertyType): List<PropertyDom> {
        return propertyDao.getAllDirtyByType(propertyType.value).map {
            propertyEntToDomMapper.map(it)
        }
    }

    override fun markPropertiesNonDirty(properties: List<PropertyDom>) {
        propertyDao.markSpecificNonDirty(propertyDomToEntMapper.map(properties))
    }

    override fun saveProperties(properties: List<PropertyDto>, propertyType: PropertyType) {
        propertyDao.saveProperties(properties, propertyType)
    }

    override fun setProperties(properties: Map<String, String?>, propertyType: PropertyType) {
        propertyDao.setProperties(properties, propertyType)
    }

    override fun setProperty(key: String, value: String, propertyType: PropertyType) {
        propertyDao.upsertOne(key, value, propertyType)
    }

    override fun getPropertyValue(key: String): String? {
        return propertyDao.getOne(key)?.value
    }

    override fun getProperty(key: String): PropertyDom?{
        val propertyEntity = propertyDao.getOne(key)
        return if(propertyEntity != null){
            propertyEntToDomMapper.map(propertyEntity)
        }else{
            null
        }
    }

    override fun getPropertiesByType(propertyType: PropertyType): Map<String, String?>{
        return propertyDao.getAllByType(propertyType.value).associate { Pair(it.id, it.value) }
    }

    override fun getPropertyLive(key: String): Flow<PropertyDom?> {
        return propertyDao.getOneLive(key).transform {
            if(it != null) {
                emit(propertyEntToDomMapper.map(it))
            }else{
                emit(null)
            }
        }
    }

    override fun getMultipleLive(keys: List<String>): Flow<List<PropertyDom?>>{
        return propertyDao.getMultipleLive(keys).transform {
            emit(propertyEntToDomMapper.map(it))
        }
    }
}
