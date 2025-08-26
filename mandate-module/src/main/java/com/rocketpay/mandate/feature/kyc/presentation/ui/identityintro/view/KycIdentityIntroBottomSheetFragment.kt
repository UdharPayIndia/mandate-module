package com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.databinding.FragmentKycIdentityIntroRpBinding
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycComponent
import com.rocketpay.mandate.feature.kyc.presentation.injection.KycStateMachineFactory
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroEvent
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroState
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroStateMachine
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.statemachine.KycIdentityIntroUSF
import com.rocketpay.mandate.feature.kyc.presentation.ui.identityintro.viewmodel.KycIdentityIntroUM
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import javax.inject.Inject

internal class KycIdentityIntroBottomSheetFragment : StateMachineBottomSheetFragment<KycIdentityIntroEvent, KycIdentityIntroState, KycIdentityIntroUSF>() {

    private lateinit var binding: FragmentKycIdentityIntroRpBinding
    private lateinit var vm: KycIdentityIntroUM
    @Inject
    internal lateinit var kycStateMachineFactory: KycStateMachineFactory

    companion object {
        const val BUNDLE_NAME = "BUNDLE_NAME"
        const val OPEN_IDENTITY_STEP = "open_identity_step"
        fun newInstance(bundle: Bundle?): KycIdentityIntroBottomSheetFragment {
            val fragment = KycIdentityIntroBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        KycComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, kycStateMachineFactory)[KycIdentityIntroStateMachine::class.java]
        vm = KycIdentityIntroUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val panName = savedInstanceState?.getString(BUNDLE_NAME)
        stateMachine.dispatchEvent(KycIdentityIntroEvent.LoadData(panName))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        expandFull()
        binding = FragmentKycIdentityIntroRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }

    override fun handleState(state: KycIdentityIntroState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: KycIdentityIntroUSF) {
        when (sideEffect) {
            KycIdentityIntroUSF.CloseBottomSheet -> {
                FragmentResultBus.fire(OPEN_IDENTITY_STEP, true)
                dismiss()
            }

        }
    }
}
