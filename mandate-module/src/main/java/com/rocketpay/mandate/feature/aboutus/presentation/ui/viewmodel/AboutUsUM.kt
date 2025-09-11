package com.rocketpay.mandate.feature.aboutus.presentation.ui.viewmodel

import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsEvent
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsState

internal class AboutUsUM(private val dispatchEvent: (AboutUsEvent) -> Unit) : BaseMainUM() {

    fun handleState(state: AboutUsState) {
    }

    fun onTermsAndConditionClick() {
        dispatchEvent(AboutUsEvent.TermsAndConditionClick)
    }

    fun onPrivacyPolicyClick() {
        dispatchEvent(AboutUsEvent.PrivacyPolicyClick)
    }

}
