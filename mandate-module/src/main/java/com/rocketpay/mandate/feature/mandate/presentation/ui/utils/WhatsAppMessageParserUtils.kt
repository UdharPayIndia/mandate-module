package com.rocketpay.mandate.feature.mandate.presentation.ui.utils

import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.WhatsAppMessageConfig
import com.rocketpay.mandate.feature.mandate.domain.entities.WhatsAppMessageData
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.viewmodel.InstallmentFrequency
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DateUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal object WhatsAppMessageParserUtils {

    private const val DEFAULT = "default"
    private const val MERCHANT_NAME = "MERCHANT_NAME"
    private const val TOTAL_AMOUNT = "TOTAL_AMOUNT"
    private const val START_DATE = "START_DATE"
    private const val LINK = "LINK"
    private const val NO_OF_INSTALLMENT = "NO_OF_INSTALLMENT"
    private const val INSTALLMENT_FREQUENCY = "INSTALLMENT_FREQUENCY"
    private const val INSTALLMENT_AMOUNT = "INSTALLMENT_AMOUNT"

    internal fun getMessageForSharePaymentLink(
        messageTemplate: String,
        mandate: Mandate,
        name: String,
    ): String {
        return messageTemplate.replace(MERCHANT_NAME, name)
            .replace(TOTAL_AMOUNT, AmountUtils.format(mandate.originalAmount))
            .replace(START_DATE, DateUtils.getDate(mandate.startAt, DateUtils.SLASH_DATE_FORMAT_WITH_TWO_DIGIT_YEAR))
            .replace(LINK, mandate.mandateUrl)
            .replace(NO_OF_INSTALLMENT, mandate.installments.toString())
            .replace(INSTALLMENT_FREQUENCY, ResourceManager.getInstance().getString(mandate.frequency.suffix_ly))
            .replace(INSTALLMENT_AMOUNT, AmountUtils.format(mandate.getMandateInstallmentAmount()))
    }

}