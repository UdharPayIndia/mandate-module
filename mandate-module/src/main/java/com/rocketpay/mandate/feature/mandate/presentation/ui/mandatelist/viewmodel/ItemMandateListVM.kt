package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.viewmodel

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.rocketpay.mandate.R
import com.rocketpay.mandate.feature.mandate.domain.entities.Mandate
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextEnum
import com.rocketpay.mandate.feature.mandate.domain.entities.SubtextUiState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.viewmodel.MandateStateUi
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.RecyclerViewAdapter
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.resourcemanager.ResourceManager

internal class ItemMandateListVM(
    val mandate: Mandate,
    val itemClick: (MandateListEvent) -> Unit
): RecyclerViewAdapter.RecyclerViewItemViewModel {
    val headerTitle = ObservableField<String>()
    val headerUrl = ObservableField<String>()
    val bodyTitle = ObservableField<String>()
    val bodySubtitle = ObservableField<String>()
    val footerTitle = ObservableField<String>()
    val footerSubtitle = ObservableField<String>()
    val footerSubtitleColor = ObservableInt()
    val footerBackgroundDrawable = ObservableField<Drawable>()
    val isUnread = ObservableBoolean()
    val nofUpdateText = ObservableField<String>("1")

    init {
        headerTitle.set(mandate.customerDetail.name)
        bodyTitle.set(mandate.customerDetail.name)
        isUnread.set(mandate.uiState == SubtextUiState.Unread)
        bodySubtitle.set(SubtextEnum.getMandateSubTextTranslation(mandate))

        val totalAmount = AmountUtils.format(mandate.getMandateAmount())
        val totalPaidAmount = AmountUtils.format(mandate.getMandatePaidAmount())
        footerTitle.set(ResourceManager.getInstance().getString(R.string.rp_mandate_list_amount, totalPaidAmount, totalAmount))

        val mandateStateUi = MandateStateUi.getMandateStateUi(mandate.state)
        footerSubtitle.set(ResourceManager.getInstance().getString(mandateStateUi.text))
        footerSubtitleColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_3))

        val mandateLightColor = ResourceManager.getInstance().getColor(mandateStateUi.background)
        footerBackgroundDrawable.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_background_card_2, mandateLightColor))
    }

    fun onItemClick() {
        itemClick(MandateListEvent.MandateClick(mandate))
    }
}
