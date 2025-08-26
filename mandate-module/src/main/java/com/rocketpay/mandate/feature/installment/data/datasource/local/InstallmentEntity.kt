package com.rocketpay.mandate.feature.installment.data.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rocketpay.mandate.feature.mandate.data.datasource.local.PaymentModeDetailEntity

@Entity(
    tableName = "installment",
    indices = [
        Index(value = ["mandate_id"]),
        Index(value = ["id", "mandate_id"])
    ]
)
internal class InstallmentEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "mandate_id")
    val mandateId: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "amountUI")
    val amountUI: Double,

    @ColumnInfo(name = "amount_without_charges")
    val amountWithoutCharges: Double,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "due_date")
    val dueDate: Long,

    @ColumnInfo(name = "installment_utr")
    val installmentUtr: String,

    @ColumnInfo(name = "serial_number")
    val serialNumber: Int,

    @ColumnInfo(name = "state")
    val state: String,

    @ColumnInfo(name = "utr")
    val utr: String?,

    @ColumnInfo("medium")
    val medium: String?,

    @Embedded
    val paymentModeDetailEntity: PaymentModeDetailEntity?,

    @ColumnInfo(name = "source")
    val source: String?,

    @ColumnInfo(name = "destination")
    val destination: String?,

    @ColumnInfo(name = "journey") var journey: String?,

    @ColumnInfo(name = "actions")
    var actions: String? = null,

    @ColumnInfo(name = "charges")
    val charges: String?,

    @ColumnInfo(name = "payment_order_id")
    val paymentOrderId: String,

    @ColumnInfo(name = "status")
    val status: String? = null,

    @Deprecated("NOT_TO_BE_USED")
    @ColumnInfo(name = "bearer")
    val bearer: String? = null,

    @Deprecated("NOT_TO_BE_USED")
    @ColumnInfo(name = "status_description")
    val statusDescription: String? = null,

    @Deprecated("NOT_TO_BE_USED")
    @ColumnInfo(name = "original_amount")
    val originalAmount: Double = 0.0,
)
