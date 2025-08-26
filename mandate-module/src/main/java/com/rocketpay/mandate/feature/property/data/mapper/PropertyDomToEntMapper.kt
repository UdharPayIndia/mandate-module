package com.rocketpay.mandate.feature.property.data.mapper

import com.rocketpay.mandate.feature.property.data.entities.PropertyEntity
import com.rocketpay.mandate.feature.property.domain.entities.PropertyDom

internal class PropertyDomToEntMapper {
    fun map(source: PropertyDom): PropertyEntity {
        return PropertyEntity(
            id = source.id,
            value = source.value,
            type = source.type,
            is_dirty = source.isDirty
        )
    }

    fun map(source: List<PropertyDom>): List<PropertyEntity> {
        return source.map { map(it) }
    }
}
