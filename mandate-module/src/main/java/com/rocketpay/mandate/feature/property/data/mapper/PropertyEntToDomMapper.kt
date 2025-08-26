package com.rocketpay.mandate.feature.property.data.mapper

import com.rocketpay.mandate.feature.property.data.entities.PropertyEntity
import com.rocketpay.mandate.feature.property.domain.entities.PropertyDom

internal class PropertyEntToDomMapper {
    fun map(source: PropertyEntity): PropertyDom {
        return PropertyDom(
            id = source.id,
            value = source.value,
            type = source.type,
            isDirty = source.is_dirty
        )
    }

    fun map(source: List<PropertyEntity>): List<PropertyDom> {
        return source.map { map(it) }
    }
}
