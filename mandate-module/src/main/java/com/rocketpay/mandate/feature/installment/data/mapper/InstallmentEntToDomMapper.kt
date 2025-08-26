package com.rocketpay.mandate.feature.installment.data.mapper

import com.google.gson.reflect.TypeToken
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.rocketpay.mandate.feature.bankaccount.data.entities.BankAccountDto
import com.rocketpay.mandate.feature.bankaccount.data.mapper.BankAccountDtoToDomMapper
import com.rocketpay.mandate.feature.bankaccount.domain.entities.BankAccount
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentWithCustomerEntity
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentAction
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentJourneyDto
import com.rocketpay.mandate.feature.installment.domain.entities.Installment
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentJourney
import com.rocketpay.mandate.feature.installment.domain.entities.InstallmentState
import com.rocketpay.mandate.feature.mandate.data.entities.ChargeResponseDto
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMedium
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMode
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class InstallmentEntToDomMapper : ListMapper<InstallmentEntity, Installment> {
    override fun map(source: InstallmentEntity): Installment {
        return Installment(
            id = source.id,
            mandateId = source.mandateId,
            amount = source.amount,
            amountUI = source.amountUI,
            amountWithoutCharges = source.amountWithoutCharges,
            dueDate = source.dueDate,
            updatedAt = source.updatedAt,
            createdAt = source.createdAt,
            serialNumber = source.serialNumber,
            installmentUtr = source.installmentUtr,
            state = InstallmentState.get(source.state),
            status = InstallmentState.get(source.status),
            journey = getInstallmentJourney(source.journey),
            utr = source.utr,
            source = getBankAccount(source.source),
            destination = getBankAccount(source.destination),
            skipEnable = isSkipEnable(source.actions),
            retryEnable = isRetryEnable(source.actions),
            chargePenalty = isChargePenaltyEnable(source.actions),
            markAsPaid = isMarkAsPaidEnable(source.actions),
            charges = getChargeResponse(source.charges),
            paymentMedium = PaymentMedium.get(source.medium),
            paymentMode = PaymentMode.get(source.paymentModeDetailEntity?.mode),
            isMerchantCollected = source.paymentModeDetailEntity?.merchantCollected == true,
            comments = source.paymentModeDetailEntity?.comments,
            paymentOrderId = source.paymentOrderId,
            retryScheduleDate = source.paymentModeDetailEntity?.retryScheduleDate.orEmpty()
        )
    }

    fun mapInstallmentWithCustomerList(source: List<InstallmentWithCustomerEntity>): List<Installment> {
        return source.map { mapInstallmentWithCustomer(it) }
    }

    fun mapInstallmentWithCustomer(source: InstallmentWithCustomerEntity): Installment{
        val installment = map(source.installment)
        installment.customerName = source.customerName
        return installment
    }

    private fun isRetryEnable(actions: String?): Boolean {
        val type = object : TypeToken<List<InstallmentAction>>(){}.type
        val tempActions = JsonConverter.getInstance().fromJson<List<InstallmentAction>>(actions, type)
        return if (tempActions == null) {
            false
        } else {
            return tempActions.firstOrNull { it.id == "retry" }?.isEnabled ?: false
        }
    }

    private fun isChargePenaltyEnable(actions: String?): Boolean? {
        val type = object : TypeToken<List<InstallmentAction>>(){}.type
        val tempActions = JsonConverter.getInstance().fromJson<List<InstallmentAction>>(actions, type)
        return if (tempActions == null) {
            false
        } else {
            return tempActions.firstOrNull { it.id == "charge_penalty" }?.isEnabled
        }
    }

    private fun isMarkAsPaidEnable(actions: String?): Boolean? {
        val type = object : TypeToken<List<InstallmentAction>>(){}.type
        val tempActions = JsonConverter.getInstance().fromJson<List<InstallmentAction>>(actions, type)
        return if (tempActions == null) {
            false
        } else {
            return tempActions.firstOrNull { it.id == "mark_paid" }?.isEnabled
        }
    }

    private fun getChargeResponse(actions: String?): ChargeResponseDto? {
        val type = object : TypeToken<ChargeResponseDto>() {}.type
        return JsonConverter.getInstance().fromJson<ChargeResponseDto>(actions, type)
    }

    private fun isSkipEnable(actions: String?): Boolean? {
        val type = object : TypeToken<List<InstallmentAction>>(){}.type
        val tempActions = JsonConverter.getInstance().fromJson<List<InstallmentAction>>(actions, type)
        return if (tempActions == null) {
            false
        } else {
            return tempActions.firstOrNull { it.id == "skip" }?.isEnabled
        }
    }

    private fun getInstallmentJourney(journey: String?): List<InstallmentJourney> {
        val type = object : TypeToken<List<InstallmentJourneyDto>>(){}.type
        return InstallmentJourneyDtoToDomMapper().mapList(JsonConverter.getInstance().fromJson(journey, type) ?: emptyList())
    }

    private fun getBankAccount(bankAccount: String?): BankAccount? {
        val bankAccountDto = JsonConverter.getInstance().fromJson(bankAccount, BankAccountDto::class.java)
        return if (bankAccountDto == null) {
            null
        } else {
            BankAccountDtoToDomMapper().map(bankAccountDto)
        }
    }
}
