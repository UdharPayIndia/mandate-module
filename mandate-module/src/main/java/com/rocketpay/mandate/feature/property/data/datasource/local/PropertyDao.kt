package com.rocketpay.mandate.feature.property.data.datasource.local

import androidx.room.*
import com.rocketpay.mandate.feature.property.data.entities.PropertyDto
import com.rocketpay.mandate.feature.property.data.entities.PropertyEntity
import com.rocketpay.mandate.feature.property.data.entities.PropertyType
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOne(propertyEntity: PropertyEntity)

    @Query("UPDATE properties SET value = :value, is_dirty = :isDirty WHERE id = :key")
    fun updateOne(key: String, value: String?, isDirty: Boolean)

    @Query("UPDATE properties SET is_dirty = :isDirty WHERE id = :key")
    fun updateDirty(key: String, isDirty: Boolean)

    @Query("SELECT * FROM properties WHERE id = :key")
    fun getOne(key: String): PropertyEntity?

    @Query("SELECT * FROM properties WHERE id = :key")
    fun getOneLive(key: String): Flow<PropertyEntity?>

    @Query("SELECT * FROM properties WHERE id in (:keys)")
    fun getMultipleLive(keys: List<String>): Flow<List<PropertyEntity>>

    @Query("SELECT * FROM properties")
    fun getAll(): List<PropertyEntity>

    @Query("SELECT * FROM properties WHERE type = :type")
    fun getAllByType(type: Int): List<PropertyEntity>

    @Query("SELECT * FROM properties WHERE is_dirty = 1")
    fun getAllDirty(): List<PropertyEntity>

    @Query("SELECT * FROM properties WHERE type =:type AND is_dirty = 1")
    fun getAllDirtyByType(type: Int): List<PropertyEntity>

    @Query("UPDATE properties SET is_dirty = 0")
    fun markAllNonDirty()

    @Transaction
    fun markSpecificNonDirty(properties: List<PropertyEntity>) {
        properties.forEach {
            updateDirty(it.id, false)
        }
    }

    @Transaction
    fun saveProperties(properties: List<PropertyDto>, propertyType: PropertyType) {
        properties.forEach {
            val propertyEntity = getOne(it.key)
            if (propertyEntity != null) {
                if (propertyEntity.is_dirty) {
                    // ignore if someone changed value during sync
                } else {
                    updateOne(it.key, it.value, false)
                }
            } else {
                val newPropertyEntity = PropertyEntity(
                    id = it.key,
                    value = it.value,
                    is_dirty = false,
                    type = propertyType.value
                )
                insertOne(newPropertyEntity)
            }
        }
    }

    @Transaction
    fun upsertOne(key: String, value: String?, propertyType: PropertyType) {
        val propertyEntity = getOne(key)
        if (propertyEntity != null) {
            val isDirty = propertyEntity.value != value || propertyEntity.is_dirty
            updateOne(key, value, isDirty)
        } else {
            val newPropertyEntity = PropertyEntity(
                id = key,
                value = value,
                is_dirty = true,
                type = propertyType.value
            )
            insertOne(newPropertyEntity)
        }
    }

    @Transaction
    fun setProperties(properties: Map<String, String?>, propertyType: PropertyType) {
        properties.forEach {
            upsertOne(it.key, it.value, propertyType)
        }
    }
}
