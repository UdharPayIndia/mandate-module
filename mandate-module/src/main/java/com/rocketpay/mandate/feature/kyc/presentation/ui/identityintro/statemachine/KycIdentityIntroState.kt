package com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine

import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class KycIdentityIntroState(val panName: String? = null) : BaseState(KycIdentityIntroScreen)

internal sealed class KycIdentityIntroEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val panName: String?): KycIdentityIntroEvent("")
    object NextClick: KycIdentityIntroEvent("owner_identity_kyc_bottomsheet_click")
}

internal sealed class KycIdentityIntroASF : AsyncSideEffect {
}


internal sealed class KycIdentityIntroUSF : UiSideEffect {
    object CloseBottomSheet : KycIdentityIntroUSF()
}

internal object KycIdentityIntroScreen : Screen("kyc_identity_intro")
