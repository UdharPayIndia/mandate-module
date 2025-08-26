package com.rocketpay.mandate.feature.mandate.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal class ChargeResponseDto(
    @SerializedName("amount") val amount: Double,
    @SerializedName("amount_without_charges") val amountWithoutCharges: Double,
    @SerializedName("per_installment_amount") val perInstallmentAmount: Double,
    @SerializedName("per_installment_amount_without_charges") val perInstallmentAmountWithoutCharges: Double,
    @SerializedName("charges") val charges: List<ChargeModel>,
    @SerializedName("per_installment_charges") val perInstallmentCharges: List<ChargeModel>,
    @SerializedName("charge_id") val chargeId: String,
    @SerializedName("discount_id") val discountId: String,
    @SerializedName("charges_at_mandate_level") val chargesAtMandateLevel: Boolean,
    @SerializedName("merchant_charges_at_mandate_level") val merchantChargesAtMandateLevel: Boolean,
    @SerializedName("customer_charges_at_mandate_level") val customerChargesAtMandateLevel: Boolean,
    @SerializedName("show_at_mandate_level") val showAtMandateLevel: Boolean,
    @SerializedName("is_bearer_control_available") var isBearerControlAvailable: Boolean = false,
    @SerializedName("bearer") var chargeBearer: String = ""
)

@Keep
internal class ChargeModel(val type: String, val charges: Double, val discount: Double)