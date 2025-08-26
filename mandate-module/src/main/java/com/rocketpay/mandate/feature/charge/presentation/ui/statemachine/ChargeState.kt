package com.rocketpay.mandate.feature.charge.presentation.ui.statemachine

import com.rocketpay.mandate.feature.charge.domain.entities.Charge
import com.rocketpay.mandate.feature.charge.presentation.ui.view.ChargeFlowType
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseEvent
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.BaseState
import com.rocketpay.mandate.common.basemodule.common.presentation.statemachine.Screen
import com.rocketpay.mandate.common.mvistatemachine.contract.AsyncSideEffect
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect

internal data class ChargeState(
    var charge: Charge? = null,
    var flowType: ChargeFlowType = ChargeFlowType.Business,
    val isCashFreeEnabled: Boolean = false
) : BaseState(ChargeScreen)


internal sealed class ChargeEvent(name: String? = null) : BaseEvent(name) {
    data class LoadData(val flowType: String?, val isCashFreeEnabled: Boolean): ChargeEvent()
    object DismissClick : ChargeEvent()
}


internal sealed class ChargeASF : AsyncSideEffect {
}


internal sealed class ChargeUSF : UiSideEffect {
    object CloseBottomSheet : ChargeUSF()
}

internal object ChargeScreen : Screen("charge")
