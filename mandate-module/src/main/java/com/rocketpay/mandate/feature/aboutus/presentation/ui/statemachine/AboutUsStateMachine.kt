package com.udharpay.feature.aboutus.presentation.ui.statemachine

import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseAnalyticsHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Next
import com.rocketpay.mandate.common.mvistatemachine.contract.collectIn
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.simple.SimpleStateMachineImpl
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsASF
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsEvent
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsState
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsUSF
import com.rocketpay.mandate.feature.aboutus.presentation.ui.statemachine.AboutUsUrls
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.utils.PropertyUtils
import kotlinx.coroutines.CoroutineScope

internal class AboutUsStateMachine(
    private val propertyUseCase: PropertyUseCase
) : SimpleStateMachineImpl<AboutUsEvent, AboutUsState, AboutUsASF, AboutUsUSF>(
    BaseAnalyticsHandler()
) {
    override fun startState(): AboutUsState {
        return AboutUsState()
    }

    override fun handleEvent(
        event: AboutUsEvent,
        state: AboutUsState
    ): Next<AboutUsState?, AboutUsASF?, AboutUsUSF?> {
        return when (event) {
            AboutUsEvent.LoadUrls -> {
                next(AboutUsASF.LoadUrls)
            }
            is AboutUsEvent.UrlsLoaded -> {
                next(state.copy(aboutUsUrls = event.aboutUsUrls))
            }
            AboutUsEvent.PrivacyPolicyClick -> {
                if (state.aboutUsUrls == null) {
                    noChange()
                } else {
                    next(AboutUsUSF.OpenUrl(state.aboutUsUrls.privacyPolicy, ResourceManager.getInstance().getString(
                        R.string.rp_privacy_policy)))
                }
            }
            AboutUsEvent.TermsAndConditionClick -> {
                if (state.aboutUsUrls == null) {
                    noChange()
                } else {
                    next(AboutUsUSF.OpenUrl(state.aboutUsUrls.termsAndCondition,
                        ResourceManager.getInstance().getString(R.string.rp_terms_n_condition)))
                }
            }
        }
    }

    override suspend fun handleAsyncSideEffect(
        sideEffect: AboutUsASF,
        dispatchEvent: (AboutUsEvent) -> Unit,
        viewModelScope: CoroutineScope
    ) {
        when (sideEffect) {
            AboutUsASF.LoadUrls -> {
                propertyUseCase.getMultiplePropertyLive(listOf(PropertyUtils.TERMS_AND_CONDITION_URL,
                    PropertyUtils.PRIVACY_POLICY_URL)).collectIn(viewModelScope){
                    val aboutUsUrls = AboutUsUrls(
                        termsAndCondition = it.find { it?.id == PropertyUtils.TERMS_AND_CONDITION_URL }?.value.orEmpty(),
                        privacyPolicy = it.find { it?.id == PropertyUtils.PRIVACY_POLICY_URL }?.value.orEmpty(),
                    )
                    dispatchEvent(AboutUsEvent.UrlsLoaded(aboutUsUrls))
                }

            }
        }
    }
}
