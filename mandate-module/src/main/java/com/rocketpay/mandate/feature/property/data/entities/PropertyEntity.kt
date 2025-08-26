package com.rocketpay.mandate.feature.property.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "properties",
)
internal data class PropertyEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "value")
    val value: String?,

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "is_dirty")
    val is_dirty: Boolean
)
