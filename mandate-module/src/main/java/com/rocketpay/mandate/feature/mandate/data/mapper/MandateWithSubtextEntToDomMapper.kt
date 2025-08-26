package com.rocketpay.mandate.feature.mandate.data.mapper

import com.rocketpay.mandate.feature.mandate.data.datasource.local.MandateWithSubtextEntity
import com.rocketpay.mandate.feature.mandate.data.entities.MetaData
import com.rocketpay.mandate.feature.mandate.domain.entities.CustomerDetail
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateState
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.mandate.domain.entities.PaymentMethodDetail
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextEnum
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextUiState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.ChargeBearer
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import com.udharpay.kernel.kernelcommon.mapper.ListMapper

internal class MandateWithSubtextEntToDomMapper : ListMapper<MandateWithSubtextEntity, Mandate> {
    override fun map(source: MandateWithSubtextEntity): Mandate {
        return Mandate(
            amount = source.mandateEntity.amount,
            amountWithoutCharges = source.mandateEntity.amountWithoutCharges,
            bearer = ChargeBearer.get(source.mandateEntity.bearer),
            chargeId = source.mandateEntity.chargeId,
            discountId = source.mandateEntity.discountId,
            dueAmount = source.mandateEntity.amountRemaining ?: 0.0,
            gatewayMandateId = source.mandateEntity.gatewayMandateId,
            paymentMethodDetail = PaymentMethodDetail(
                PaymentMethod.get(source.mandateEntity.paymentMethodDetailEntity.method),
                source.mandateEntity.paymentMethodDetailEntity.upiId
            ),
            nextChargeAt = source.mandateEntity.nextChargeAt,
            mandateUrl = source.mandateEntity.mandateUrl,
            installmentAmount = source.mandateEntity.installmentAmount,
            createdAt = source.mandateEntity.createdAt,
            startAt = source.mandateEntity.startAt,
            endAt = source.mandateEntity.endAt,
            frequency = InstallmentFrequency.get(source.mandateEntity.frequency),
            installmentsPaid = source.mandateEntity.installmentsPaid,
            updatedAt = source.mandateEntity.updatedAt,
            installments = source.mandateEntity.installments,
            id = source.mandateEntity.id,
            customerDetail = CustomerDetail(
                source.mandateEntity.customerDetailEntity.name,
                source.mandateEntity.customerDetailEntity.mobileNumber
            ),
            state = MandateState.get(source.mandateEntity.state),
            statusDescription = source.mandateEntity.statusDescription ?: "",
            description = source.mandateEntity.description,
            product = MandateProduct.get(source.mandateEntity.product),
            uiState = SubtextUiState.get(source.mandateSubtextEntity.uiState),
            subTextEnum = SubtextEnum.get(source.mandateSubtextEntity.subtext),
            originalAmount = source.mandateEntity.originalAmount,
            meta = getMetaData(source.mandateEntity.meta),
            isDeleted = source.mandateEntity.isDeleted,
            referenceId = source.mandateEntity.referenceId,
            referenceType = source.mandateEntity.referenceType
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
