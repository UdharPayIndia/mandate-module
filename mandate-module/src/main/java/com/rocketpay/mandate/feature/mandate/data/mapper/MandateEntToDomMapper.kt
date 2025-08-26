package com.rocketpay.mandate.feature.mandate.data.mapper

import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateEntity
import com.rocketpay.mandate.feature.mandate.data.entities.MetaData
import com.rocketpay.mandate.feature.mandate.domain.entities.CustomerDetail
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethodDetail
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class MandateEntToDomMapper : ListMapper<MandateEntity, Mandate> {
    override fun map(source: MandateEntity): Mandate {
        return Mandate(
            amount = source.amount,
            amountWithoutCharges = source.amountWithoutCharges,
            bearer = ChargeBearer.get(source.bearer),
            chargeId = source.chargeId,
            discountId = source.discountId,
            dueAmount = source.amountRemaining,
            gatewayMandateId = source.gatewayMandateId,
            paymentMethodDetail = PaymentMethodDetail(
                PaymentMethod.get(source.paymentMethodDetailEntity.method),
                source.paymentMethodDetailEntity.upiId
            ),
            nextChargeAt = source.nextChargeAt,
            mandateUrl = source.mandateUrl,
            installmentAmount = source.installmentAmount,
            createdAt = source.createdAt,
            startAt = source.startAt,
            endAt = source.endAt,
            frequency = InstallmentFrequency.get(source.frequency),
            installmentsPaid = source.installmentsPaid,
            updatedAt = source.updatedAt,
            installments = source.installments,
            id = source.id,
            customerDetail = CustomerDetail(
                source.customerDetailEntity.name,
                source.customerDetailEntity.mobileNumber
            ),
            state = MandateState.get(source.state),
            statusDescription = source.statusDescription ?: "",
            description = source.description,
            product = MandateProduct.get(source.product),
            originalAmount = source.originalAmount,
            meta = getMetaData(source.meta),
            referenceId = source.referenceId,
            referenceType = source.referenceType,
            isDeleted = source.isDeleted
        )
    }

    private fun getMetaData(meta: String): MetaData? {
        return if (meta.isEmpty()) {
            null
        } else {
            val metaData = JsonConverter.getInstance().fromJson(meta, MetaData::class.java)
            if (metaData == null) {
                null
            } else {
                metaData
            }
        }
    }
}
