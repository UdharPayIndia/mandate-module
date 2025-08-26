package com.rocketpay.mandate.feature.mandate.data.mapper

import com.rocketpay.mandate.feature.mandate.data.datasource.local.CustomerDetailEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.PaymentMethodDetailEntity
import com.rocketpay.mandate.feature.mandate.data.entities.MandateDto
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.double
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.int
import com.rocketpay.mandate.common.basemodule.common.presentation.ext.long
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class MandateDtoToEntMapper : ListMapper<MandateDto, MandateEntity> {
    override fun map(source: MandateDto): MandateEntity {
        return MandateEntity(
            amount = source.amount.double(),
            amountWithoutCharges = source.amountWithoutCharges.double(),
            bearer = source.bearer,
            chargeId = source.chargeId,
            discountId = source.discountId,
            amountRemaining = source.amount.double(),
            gatewayMandateId = source.gatewayMandateId,
            paymentMethodDetailEntity = PaymentMethodDetailEntity(
                source.paymentDetails.method,
                source.paymentDetails.upiId
            ),
            nextChargeAt = source.nextChargeAt.long(),
            mandateUrl = source.mandateUrl ?: "",
            installmentAmount = source.installmentAmount.double(),
            createdAt = source.createdAt.long(),
            startAt = source.startAt.long(),
            endAt = source.endAt.long(),
            frequency = source.frequency ?: "",
            installmentsPaid = 0,
            updatedAt = source.updatedAt.long(),
            installments = source.installments.int(),
            id = source.id,
            customerDetailEntity = CustomerDetailEntity(
                source?.customer?.name ?: "",
                source?.customer?.mobileNumber ?: ""
            ),
            state = source.status ?: "",
            isDeleted = source.isDeleted,
            status = source.status,
            statusDescription = "",
            description = source.description ?: "",
            actions = JsonConverter.getInstance().toJson(source.actions),
            product = source.product,
            isSelfMandate = false,
            originalAmount = source.originalAmount.double(),
            meta = JsonConverter.getInstance().toJson(source.meta) ?: "",
            referenceId = source.referenceId,
            referenceType = source.referenceType
        )
    }
}
