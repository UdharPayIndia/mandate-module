package com.rocketpay.mandate.feature.settlements.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class PaymentOrderDto(
    @SerializedName("id") val id: String,
    @SerializedName("deleted") val isDeleted: Boolean,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("updated_at") val updatedAt: Long,
    @SerializedName("type") val type: String,
    @SerializedName("state") val state: String,
    @SerializedName("server_seq") val serverSequence: Long,
    @SerializedName("reference_id") val referenceId: String,
    @SerializedName("payees") val payees: List<PaymentOrderSummaryDto>,
    @SerializedName("meta") val meta: PaymentOrderMetaDto
)

@Keep
class PaymentOrderSummaryDto(
    @SerializedName("account_id") val accountId: String,
    @SerializedName("amount") val amount: PaymentOrderAmountDto,
    @SerializedName("tag") val tag: String
)

@Keep
class PaymentOrderAmountDto(
    @SerializedName("value") val value: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("unit") val unit: String
)

@Keep
class PaymentOrderMetaDto(
    @SerializedName("utr") val utr: String?,
    @SerializedName("instrument_details") val instrumentDetails: InstrumentDetailsDto?,
)

@Keep
class InstrumentDetailsDto(
    @SerializedName("ifsc") val ifsc: String?,
    @SerializedName("bank_code") val bankCode: String?,
    @SerializedName("bank_name") val bankName: String?,
    @SerializedName("account_number") val accountNumber: String?,
    @SerializedName("instrument_type") val instrumentType: String?,
    @SerializedName("account_holder_name_at_bank") val accountHolderNameAtBank: String?,
)