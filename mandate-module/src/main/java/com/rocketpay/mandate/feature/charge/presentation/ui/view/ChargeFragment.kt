package com.rocketpay.mandate.feature.charge.presentation.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.databinding.FragmentChargeRpBinding
import com.rocketpay.mandate.feature.charge.presentation.injection.ChargeComponent
import com.rocketpay.mandate.feature.charge.presentation.injection.ChargeStateMachineFactory
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeEvent
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeSimpleStateMachine
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeState
import com.rocketpay.mandate.feature.charge.presentation.ui.statemachine.ChargeUSF
import com.rocketpay.mandate.feature.charge.presentation.ui.viewmodel.ChargeUM
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import javax.inject.Inject

internal class ChargeFragment : StateMachineBottomSheetFragment<ChargeEvent, ChargeState, ChargeUSF>() {

    private lateinit var binding: FragmentChargeRpBinding
    private lateinit var vm: ChargeUM
    @Inject
    internal lateinit var chargeStateMachineFactory: ChargeStateMachineFactory

    companion object {
        const val FLOW_TYPE = "flow_type"
        const val IS_CASH_FREE_ENABLED = "is_cash_free_enabled"

        fun newInstance(bundle: Bundle?): ChargeFragment {
            val chargeFragment = ChargeFragment()
            chargeFragment.arguments = bundle
            return chargeFragment
        }
    }

    override fun injection() {
        super.injection()
        ChargeComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, chargeStateMachineFactory)[ChargeSimpleStateMachine::class.java]
        vm = ChargeUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val isCashFreeEnabled = savedInstanceState?.getBoolean(IS_CASH_FREE_ENABLED) ?: false
        val chargeFlowType = savedInstanceState?.getString(FLOW_TYPE)
        stateMachine.dispatchEvent(ChargeEvent.LoadData(chargeFlowType, isCashFreeEnabled))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChargeRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }

    override fun handleState(state: ChargeState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: ChargeUSF) {
        when (sideEffect) {
            ChargeUSF.CloseBottomSheet -> {
                dismiss()
            }
        }
    }
}
