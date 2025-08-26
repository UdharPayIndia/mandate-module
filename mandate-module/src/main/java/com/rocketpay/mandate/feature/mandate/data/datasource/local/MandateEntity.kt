package com.rocketpay.mandate.feature.mandate.data.datasource.local

import androidx.room.*

@Entity(
    tableName = "mandate",
    indices = []
)
internal class MandateEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "amount_without_charges")
    val amountWithoutCharges: Double,

    @ColumnInfo(name = "bearer")
    val bearer: String?,

    @ColumnInfo(name = "chargeId")
    val chargeId: String?,

    @ColumnInfo(name = "discountId")
    val discountId: String?,

    @ColumnInfo(name = "amount_remaining")
    var amountRemaining: Double,

    @ColumnInfo(name = "gateway_mandate_id")
    val gatewayMandateId: String?,

    @Embedded
    val paymentMethodDetailEntity: PaymentMethodDetailEntity,

    @ColumnInfo(name = "next_charge_at")
    val nextChargeAt: Long,

    @ColumnInfo(name = "mandate_url")
    val mandateUrl: String,

    @ColumnInfo(name = "installment_amount")
    val installmentAmount: Double,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "start_at")
    val startAt: Long,

    @ColumnInfo(name = "end_at")
    val endAt: Long,

    @ColumnInfo(name = "frequency")
    val frequency: String,

    @ColumnInfo(name = "installments_paid") var installmentsPaid: Int,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,

    @ColumnInfo(name = "installments")
    val installments: Int,

    @Embedded
    val customerDetailEntity: CustomerDetailEntity,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean,

    @ColumnInfo(name = "state")
    val state: String,

    @Deprecated("NOT_TO_BE_USED")
    @ColumnInfo(name = "status_description")
    val statusDescription: String?,

    @ColumnInfo(name = "actions")
    val actions: String?,

    @ColumnInfo(name = "product")
    val product: String?,

    @Deprecated("NOT_TO_BE_USED")
    @ColumnInfo(name = "is_self_mandate")
    val isSelfMandate: Boolean?,

    @ColumnInfo(name = "original_amount")
    val originalAmount: Double,

    @ColumnInfo(name = "meta")
    val meta: String,

    @ColumnInfo(name = "reference_id")
    val referenceId: String?,

    @ColumnInfo(name = "reference_type")
    val referenceType: String?,
){
    @Ignore
    var isUpdated: Boolean = false
}
