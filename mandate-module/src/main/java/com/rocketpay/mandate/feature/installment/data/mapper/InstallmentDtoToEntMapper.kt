package com.rocketpay.mandate.feature.installment.data.mapper

import com.rocketpay.mandate.feature.installment.data.datasource.local.InstallmentEntity
import com.rocketpay.mandate.feature.installment.data.entities.InstallmentDto
import com.rocketpay.mandate.feature.mandate.data.datasource.local.PaymentModeDetailEntity
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.ifNullOrEmpty
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class InstallmentDtoToEntMapper : ListMapper<InstallmentDto, InstallmentEntity> {

    override fun map(source: InstallmentDto): InstallmentEntity {
        return InstallmentEntity(
            id = source.id,
            mandateId = source.mandate_id,
            amount = source.amount,
            amountUI = source.amount,
            amountWithoutCharges = source.amount_without_charges.double(),
            dueDate = source.due_date,
            updatedAt = source.updated_at,
            createdAt = source.created_at,
            utr = source.utr,
            serialNumber = source.serial_number,
            state = source.state ?: "",
            status = source.status,
            journey = source.journey.takeIf { !it.isNullOrEmpty() }?.let{ JsonConverter.getInstance().toJson(source.journey)},
            source = source.source.takeIf { it != null }?.let{ JsonConverter.getInstance().toJson(source.source)},
            destination =  source.destination.takeIf { it != null }?.let{JsonConverter.getInstance().toJson(source.destination)},
            installmentUtr = source.installment_utr ?: "",
            charges = source.charges.takeIf { it != null }?.let{ JsonConverter.getInstance().toJson(source.charges)},
            paymentOrderId = source.payment_order_id.ifNullOrEmpty(""),
            medium = source.medium,
            paymentModeDetailEntity = PaymentModeDetailEntity(
                source.meta?.mode, source.meta?.merchant_collected == true,
                source.meta?.comments,
                source.meta?.retry_schedule_date.orEmpty()
            )
        )
    }
}
