package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel

import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.MandateProduct
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemMandateHeaderVM (
    val mandate: Mandate?
) : RecyclerViewAdapter.RecyclerViewItemViewModel {

    val installmentInfo = ObservableField<String>()

    init {
        mandate?.let {
            val amountValue = mandate.getMandateAmount()

            if (mandate.product is MandateProduct.Khata) {
                installmentInfo.set("")
            } else {

                val totalAmount = AmountUtils.format(amountValue)
                val perInstallmentAmount = AmountUtils.format(mandate.getMandateInstallmentAmount())
                val installmentPerText = ResourceManager.getInstance().getString(mandate.frequency.suffix_s)
                val totalNumberOfInstallment = mandate.installments
                installmentInfo.set(ResourceManager.getInstance().getString(R.string.rp_payment_installment_info, perInstallmentAmount, totalNumberOfInstallment, installmentPerText, totalAmount))
            }
        }
    }
}
