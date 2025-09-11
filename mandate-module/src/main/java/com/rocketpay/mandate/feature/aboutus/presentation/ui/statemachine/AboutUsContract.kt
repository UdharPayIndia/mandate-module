package com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class AboutUsState(
    val aboutUsUrls: AboutUsUrls? = null
) : BaseState(AboutUsScreen)

data class AboutUsUrls(
    val termsAndCondition: String,
    val privacyPolicy: String,
)

internal sealed class AboutUsEvent(name: String? = null) : BaseEvent(name) {
    object LoadUrls : AboutUsEvent()
    data class UrlsLoaded(val aboutUsUrls: AboutUsUrls) : AboutUsEvent()
    object TermsAndConditionClick: AboutUsEvent("terms_and_condition_click")
    object PrivacyPolicyClick: AboutUsEvent("privacy_policy_click")
}

internal sealed class AboutUsASF : AsyncSideEffect {
    object LoadUrls: AboutUsASF()
}

internal sealed class AboutUsUSF : UiSideEffect {
    data class OpenUrl(val url: String, val title: String): AboutUsUSF()
}

internal object AboutUsScreen : Screen("about_us")
