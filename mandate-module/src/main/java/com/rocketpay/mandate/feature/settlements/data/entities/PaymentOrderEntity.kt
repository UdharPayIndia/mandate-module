package com.rocketpay.mandate.feature.settlements.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "paymentOrder",
    indices = []
)
internal class PaymentOrderEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "server_sequence")
    val serverSequence: Long,

    @ColumnInfo(name = "reference_id")
    val referenceId: String,

     @ColumnInfo(name = "payeesString")
    val payeesString: String,

    @ColumnInfo(name = "metaString")
    val metaString: String
){
    @ColumnInfo(name = "referenceString")
    var referenceString: String = ""
}