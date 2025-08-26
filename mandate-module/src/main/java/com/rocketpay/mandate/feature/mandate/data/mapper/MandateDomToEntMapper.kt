package com.rocketpay.mandate.feature.mandate.data.mapper

import com.rocketpay.mandate.feature.mandate.data.datasource.local.CustomerDetailEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.datasource.local.PaymentMethodDetailEntity
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class MandateDomToEntMapper : ListMapper<Mandate, MandateEntity> {
    override fun map(source: Mandate): MandateEntity {
        return MandateEntity(
            amount = source.amount,
            amountWithoutCharges = source.amountWithoutCharges,
            bearer = source.bearer?.value,
            chargeId = source.chargeId,
            discountId = source.discountId,
            amountRemaining = source.dueAmount,
            gatewayMandateId = source.gatewayMandateId,
            paymentMethodDetailEntity = PaymentMethodDetailEntity(
                source.paymentMethodDetail.method.value,
                source.paymentMethodDetail.upiId
            ),
            nextChargeAt = source.nextChargeAt,
            mandateUrl = source.mandateUrl,
            installmentAmount = source.installmentAmount,
            createdAt = source.createdAt,
            startAt = source.startAt,
            endAt = source.endAt,
            frequency = source.frequency.value,
            installmentsPaid = source.installmentsPaid,
            updatedAt = source.updatedAt,
            installments = source.installments,
            id = source.id,
            customerDetailEntity = CustomerDetailEntity(
                source.customerDetail.name,
                source.customerDetail.mobileNumber
            ),
            state = source.state.value,
            status = source.state.value,
            statusDescription = source.statusDescription ?: "",
            description = source.description,
            actions = null,
            product = source.product.value,
            isSelfMandate = null,
            isDeleted = source.isDeleted,
            originalAmount = source.originalAmount,
            referenceId = source.referenceId,
            referenceType = source.referenceType,
            meta = JsonConverter.getInstance().toJson(source.meta) ?: "",
        )
    }

}
