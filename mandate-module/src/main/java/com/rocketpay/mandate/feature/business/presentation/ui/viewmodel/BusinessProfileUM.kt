package com.rocketpay.mandate.feature.business.presentation.ui.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileEvent
import com.rocketpay.mandate.feature.business.presentation.ui.statemachine.BusinessProfileState

internal class BusinessProfileUM(private val dispatchEvent: (BusinessProfileEvent) -> Unit) : BaseMainUM() {

    val primaryActionEnabled = ObservableBoolean()
    val primaryActionText = ObservableField<String>()

    val secondaryActionEnabled = ObservableBoolean()
    val secondaryActionText = ObservableField<String>()

    val actionHelperText = ObservableField<String>()

    fun onPrimaryCtaClick() {
        dispatchEvent(BusinessProfileEvent.SaveBusinessProfile)
    }

    fun onSecondaryCtaClick() {
        dispatchEvent(BusinessProfileEvent.SkipBusinessProfile)
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    fun handleState(state: BusinessProfileState) {
        toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_business_info))
        actionHelperText.set("")

        primaryActionText.set(ResourceManager.getInstance().getString(R.string.rp_submit))
        primaryActionEnabled.set(state.primaryActionEnable)

        secondaryActionText.set(ResourceManager.getInstance().getString(R.string.rp_skip))
        secondaryActionEnabled.set(false)
    }
}
