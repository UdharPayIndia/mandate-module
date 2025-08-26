package com.rocketpay.mandate.feature.mandate.data.mapper

import com.rocketpay.mandate.feature.mandate.data.entities.CreateMandateDto
import com.rocketpay.mandate.feature.mandate.data.entities.CustomerDto
import com.rocketpay.mandate.feature.mandate.data.entities.PaymentDetailsDto
import com.rocketpay.mandate.feature.mandate.domain.entities.CreateMandate
import com.udharpay.kernel.kernelcommon.mapper.Mapper

internal class CreateMandateMapper : Mapper<CreateMandate, CreateMandateDto> {
    override fun map(source: CreateMandate): CreateMandateDto {
        return CreateMandateDto(
            amount = source.amount,
            installments = source.installments,
            customer = CustomerDto(source.customerDetail.name, source.customerDetail.mobileNumber),
            paymentDetails = PaymentDetailsDto(
                source.paymentMethodDetail.method.value,
                source.paymentMethodDetail.upiId
            ),
            description = source.description,
            frequency = source.frequency,
            startAt = source.startAt,
            product = source.product,
            bearer = source.bearer,
            chargeId = source.chargeId,
            discountId = source.discountId,
            amountWithoutCharges = source.amountWithoutCharges,
            originalAmount = source.originalAmount,
            meta = source.meta,
            referenceId = source.referenceId,
            referenceType = source.referenceType
        )
    }
}
