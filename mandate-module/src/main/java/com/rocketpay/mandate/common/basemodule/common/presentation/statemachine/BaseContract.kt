package com.rocketpay.mandate.common.basemodule.common.presentation.statemachine

import com.rocketpay.mandate.common.mvistatemachine.contract.Event
import com.rocketpay.mandate.common.mvistatemachine.contract.State

internal open class Screen(val name: String)

internal abstract class BaseEvent(var name: String? = null) : Event

internal abstract class BaseState(val screen: Screen) : State
