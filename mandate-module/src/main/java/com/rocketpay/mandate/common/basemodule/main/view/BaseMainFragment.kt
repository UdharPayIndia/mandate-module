package com.rocketpay.mandate.common.basemodule.main.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.rocketpay.mandate.common.basemodule.common.eventbus.activityresultcallback.FragmentResultBus
import com.rocketpay.mandate.common.basemodule.main.viewmodel.BaseMainUM
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineFragment
import com.rocketpay.mandate.common.mvistatemachine.contract.Event
import com.rocketpay.mandate.common.mvistatemachine.contract.State
import com.rocketpay.mandate.common.mvistatemachine.contract.UiSideEffect
import com.rocketpay.mandate.databinding.FragmentBaseMainRpBinding
import com.rocketpay.mandate.main.init.MandateLauncher
import com.rocketpay.mandate.main.init.MandateManager
import androidx.core.net.toUri

internal abstract class BaseMainFragment<E: Event, S: State, USF: UiSideEffect> : StateMachineFragment<E, S, USF>() {

    private lateinit var baseMainBinding: FragmentBaseMainRpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        baseMainBinding = FragmentBaseMainRpBinding.inflate(inflater, container, false)
        (baseMainBinding.mainLayout as ViewGroup).addView(createView(inflater, container, savedInstanceState)!!)
        return baseMainBinding.root
    }

    open fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }

    open fun setupToolbar(baseMainUM: BaseMainUM) {
        setupToolbar(baseMainUM, true)
    }

    open fun setupToolbar(baseMainUM: BaseMainUM, setUpToolbar: Boolean){
        if (::baseMainBinding.isInitialized) {
            if(setUpToolbar) {
                listener?.updateToolbar(baseMainBinding.toolbar)
                baseMainBinding.toolbar.setNavigationOnClickListener { onBackPress() }
            }
            baseMainBinding.vm = baseMainUM
        }
    }

    override fun deInitView() {
        super.deInitView()
        if (::baseMainBinding.isInitialized) {
            removeViews(baseMainBinding)
        }
    }

    fun removeViews(viewDataBinding: ViewDataBinding) {
        viewDataBinding.unbind()
        (viewDataBinding.root as ViewGroup).removeAllViews()
    }

    fun handleContactUsClick(){
        val customerSupportNumber = MandateManager.getInstance().getCustomerCareNumber()
        if(!customerSupportNumber.isNullOrEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL, "tel:${customerSupportNumber}".toUri())
            startActivity(intent)
        }else{
            FragmentResultBus.fire(MandateLauncher.HELP_ACTION_TRIGGERED, null)
        }
    }
}
