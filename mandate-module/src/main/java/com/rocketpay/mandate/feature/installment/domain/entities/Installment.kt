package com.rocketpay.mandate.feature.installment.domain.entities

import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.installment.presentation.ui.installmentdetail.viewmodel.InstallmentStateUi
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMedium
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils

internal data class Installment(
	var id: String,
	var mandateId: String,
	val amount: Double,
	val amountUI: Double,
	val amountWithoutCharges: Double,
	var dueDate: Long,
	var createdAt: Long,
	var updatedAt: Long,
	val serialNumber: Int,
	var state: InstallmentState,
	val journey: List<InstallmentJourney>,
	val utr: String?,
	val status: InstallmentState?,
	val source: BankAccount?,
	val destination: BankAccount?,
	val installmentUtr: String,
	val skipEnable: Boolean?,
	val retryEnable: Boolean,
	val chargePenalty: Boolean?,
	val markAsPaid: Boolean?,
	val charges: ChargeResponseDto?,
	val paymentMedium: PaymentMedium?,
	val paymentMode: PaymentMode?,
	val isMerchantCollected: Boolean,
	val comments: String?,
	val paymentOrderId: String,
	var customerName: String? = null,
	val retryScheduleDate: String
){

	fun getInstallmentAmount(bearer: ChargeBearer?): Double {
		return if (paymentMedium != PaymentMedium.Manual) {
			when {
				charges?.customerChargesAtMandateLevel == true || charges?.merchantChargesAtMandateLevel == true -> {
					amount
				}

				bearer == ChargeBearer.Both -> {
					val merchantCharges =
						charges?.perInstallmentCharges?.find { it.type == ChargeBearer.Merchant.value }
					if (merchantCharges != null) {
						amountWithoutCharges + merchantCharges.charges - merchantCharges.discount
					} else {
						amountWithoutCharges
					}
				}

				bearer == ChargeBearer.Customer -> {
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

	fun getInstallmentStatusUi(isManual: Boolean): InstallmentStateUi {
		return if(isManual){
			if(isMerchantCollected){
				InstallmentStateUi.ManuallyCollected
			}else if(dueDate < DateUtils.getCurrentDateWithoutTimeInMillis()){
				InstallmentStateUi.Outstanding
			}else{
				InstallmentStateUi.Created
			}
		}else{
			if(isMerchantCollected){
				InstallmentStateUi.ManuallyCollected
			} else if(status == InstallmentState.Skipped){
				InstallmentStateUi.CollectionSkipped
			}else{
				InstallmentStateUi.get(state, false)
			}
		}
	}
}

