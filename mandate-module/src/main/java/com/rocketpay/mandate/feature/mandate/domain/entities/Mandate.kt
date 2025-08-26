package com.rocketpay.mandate.feature.mandate.domain.entities

import com.rocketpay.mandate.feature.mandate.data.entities.MetaData
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency

internal class Mandate(
    val id: String,
    val amount: Double,
    val amountWithoutCharges: Double,
    val bearer: ChargeBearer?,
    val chargeId: String?,
    val discountId: String?,
    var dueAmount: Double,
    val description: String,
    val installmentAmount: Double,
    val gatewayMandateId: String?,
    val paymentMethodDetail: PaymentMethodDetail,
    val nextChargeAt: Long,
    val mandateUrl: String,
    val frequency: InstallmentFrequency,
    var installmentsPaid: Int,
    val startAt: Long,
    val endAt: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val installments: Int,
    val customerDetail: CustomerDetail,
    val state: MandateState,
    val statusDescription: String,
    val product: MandateProduct,
    val uiState: SubtextUiState? = null,
    val subTextEnum: SubtextEnum? = null,
    val originalAmount: Double,
    val referenceId: String?,
    val referenceType: String?,
    val meta: MetaData?,
    val isDeleted: Boolean
){

    fun getMandateAmount(): Double {
        return if(paymentMethodDetail.method != PaymentMethod.Manual) {
            when (bearer) {
                ChargeBearer.Both -> {
                    val merchantCharges =
                        meta?.charges?.charges?.find { it.type == ChargeBearer.Merchant.value }
                    if (merchantCharges != null) {
                        amountWithoutCharges + merchantCharges.charges - merchantCharges.discount
                    } else {
                        amountWithoutCharges
                    }
                }

                ChargeBearer.Customer -> {
                    amountWithoutCharges
                }

                else -> {
                    amount
                }
            }
        }else{
            amount
        }
    }

    fun getMandatePaidAmount(): Double {
        return if(paymentMethodDetail.method != PaymentMethod.Manual) {
            when {
                meta?.charges?.customerChargesAtMandateLevel == true || meta?.charges?.merchantChargesAtMandateLevel == true -> {
                    amount - dueAmount
                }

                bearer == ChargeBearer.Both -> {
                    val merchantCharges =
                        meta?.charges?.charges?.find { it.type == ChargeBearer.Merchant.value }
                    val totalCustomerCharges = if (merchantCharges != null) {
                        amount - amountWithoutCharges - merchantCharges.charges + merchantCharges.discount
                    } else {
                        amount - amountWithoutCharges
                    }
                    val customerChargePerInstallment = totalCustomerCharges / installments
                    val customerChargePaid = installmentsPaid * customerChargePerInstallment
                    amount - dueAmount - customerChargePaid
                }

                bearer == ChargeBearer.Customer -> {
                    val totalCharges = amount - amountWithoutCharges
                    val chargePerInstallment = totalCharges / installments
                    val chargePaid = installmentsPaid * chargePerInstallment
                    amount - dueAmount - chargePaid
                }

                else -> {
                    amount - dueAmount
                }
            }
        }else{
            amount - dueAmount
        }
    }

    fun getMandateDueAmount(): Double {
        return if(paymentMethodDetail.method != PaymentMethod.Manual) {
            when {
                meta?.charges?.customerChargesAtMandateLevel == true || meta?.charges?.merchantChargesAtMandateLevel == true -> {
                    if (bearer == ChargeBearer.Merchant) {
                        dueAmount
                    } else {
                        val merchantCharges =
                            meta?.charges?.charges?.find { it.type == ChargeBearer.Merchant.value }
                        val totalCustomerCharges = if (merchantCharges != null) {
                            amount - amountWithoutCharges - merchantCharges.charges + merchantCharges.discount
                        } else {
                            amount - amountWithoutCharges
                        }
                        dueAmount - totalCustomerCharges
                    }
                }

                bearer == ChargeBearer.Both -> {
                    val merchantCharges =
                        meta?.charges?.charges?.find { it.type == ChargeBearer.Merchant.value }
                    val totalCustomerCharges = if (merchantCharges != null) {
                        amount - amountWithoutCharges - merchantCharges.charges + merchantCharges.discount
                    } else {
                        amount - amountWithoutCharges
                    }
                    val customerChargePerInstallment = totalCustomerCharges / installments
                    val customerChargeDue =
                        (installments - installmentsPaid) * customerChargePerInstallment
                    dueAmount - customerChargeDue
                }

                bearer == ChargeBearer.Customer -> {
                    val totalCharges = amount - amountWithoutCharges
                    val chargePerInstallment = totalCharges / installments
                    val chargeDue = (installments - installmentsPaid) * chargePerInstallment
                    dueAmount - chargeDue
                }

                else -> {
                    dueAmount
                }
            }
        }else{
            dueAmount
        }
    }

    fun getMandateInstallmentAmount(): Double {
        return getMandateInstallmentAmount(amount, amountWithoutCharges,
            installmentAmount, installments, bearer, paymentMethodDetail.method, meta)
    }

    companion object{
        fun getMandateInstallmentAmount(amount: Double, amountWithoutCharges: Double,
                                        installmentAmount: Double, installments: Int, bearer: ChargeBearer?,
                                        paymentMethod: PaymentMethod?,
                                        meta: MetaData?): Double {
            return if(paymentMethod != PaymentMethod.Manual) {
                when {
                    meta?.charges?.customerChargesAtMandateLevel == true || meta?.charges?.merchantChargesAtMandateLevel == true -> {
                        installmentAmount
                    }

                    bearer == ChargeBearer.Both -> {
                        val merchantCharges =
                            meta?.charges?.charges?.find { it.type == ChargeBearer.Merchant.value }
                        val totalCustomerCharges = if (merchantCharges != null) {
                            amount - amountWithoutCharges - merchantCharges.charges + merchantCharges.discount
                        } else {
                            amount - amountWithoutCharges
                        }
                        val customerChargePerInstallment = totalCustomerCharges / installments
                        installmentAmount - customerChargePerInstallment
                    }

                    bearer == ChargeBearer.Customer -> {
                        val totalCharges = amount - amountWithoutCharges
                        val chargePerInstallment = totalCharges / installments
                        installmentAmount - chargePerInstallment
                    }

                    else -> {
                        installmentAmount
                    }
                }
            }else{
                installmentAmount
            }
        }
    }

}
