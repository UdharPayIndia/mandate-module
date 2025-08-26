package com.rocketpay.mandate.common.basemodule.statemachine.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rocketpay.mandate.common.basemodule.common.BackPressListener
import com.rocketpay.mandate.common.basemodule.common.BaseFragmentListener
import com.rocketpay.mandate.common.basemodule.common.CoreExecutionSequence
import com.rocketpay.mandate.common.basemodule.statemachine.core.StateHandler
import com.rocketpay.mandate.common.basemodule.statemachine.core.UiSideEffectHandler
import com.rocketpay.mandate.common.mvistatemachine.contract.Event
import com.rocketpay.mandate.common.mvistatemachine.contract.State
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.common.mvistatemachine.viewmodel.StateMachine


internal abstract class StateMachineBottomSheetFragment<E: Event, S: State, USF: UiSideEffect> : BottomSheetDialogFragment(),
    CoreExecutionSequence, UiSideEffectHandler<USF>, StateHandler<S>, BackPressListener {

    lateinit var stateMachine: StateMachine<E, S, USF>
    var listener: BaseFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseFragmentListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        create(arguments, savedInstanceState)
    }

    @Suppress("UNCHECKED_CAST")
    protected fun injectStateMachine() {
//        if (!::stateMachineFactory.isInitialized) {
//            stateMachineFactory = StateMachineFactory(stateMachine)
//            stateMachine = ViewModelProvider(this, stateMachineFactory)[StateMachine::class.java] as StateMachine<E, S, USF>
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated(view, savedInstanceState)
    }

    override fun registerListener() {
        super.registerListener()
        registerUiSideEffectObserver()
        registerStateObserver()
    }

    override fun registerUiSideEffectObserver() {
        stateMachine.getUiSideEffect().observe(viewLifecycleOwner, Observer {
            handleUiSideEffect(it)
        })
    }

    override fun registerStateObserver() {
        stateMachine.getState().observe(viewLifecycleOwner, Observer {
            handleState(it)
        })
    }

    override fun handleState(state: S) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        destroyView()
    }

    protected fun expandFull(isCancelable: Boolean = false) {
        dialog?.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            d.setCanceledOnTouchOutside(isCancelable)
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(it).isDraggable = false
            }
        }
    }


    override fun isBackPressHandled(): Boolean {
        return false
    }

    override fun onBackPress() {
        listener?.onBackPressed()
    }
}
