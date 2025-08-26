package com.rocketpay.mandate.feature.settlements.presentation.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentSettlementMainRpBinding
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementStateMachineFactory
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.adapter.SettlementMainAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainState
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainUSF
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.viewmodel.SettlementMainUM
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class SettlementMainFragment : BaseMainFragment<SettlementMainEvent, SettlementMainState, SettlementMainUSF>(){

    private lateinit var settlementMainAdapter: SettlementMainAdapter
    private lateinit var binding: FragmentSettlementMainRpBinding
    private lateinit var vm: SettlementMainUM
    private var movedToDefault = false
    @Inject
    internal lateinit var settlementStateMachineFactory: SettlementStateMachineFactory
    private var selectedIndex: Int = 0

    companion object {
        const val BUNDLE_SELECTED_INDEX = "index"

        fun newInstance(bundle: Bundle?): SettlementMainFragment {
            val fragment = SettlementMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        SettlementComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, settlementStateMachineFactory)[SettlementMainStateMachine::class.java]
        vm = SettlementMainUM { stateMachine.dispatchEvent(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_support_white_rp, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_support ->{
                handleContactUsClick()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun handleState(state: SettlementMainState) {
        vm.handleState(state)
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        if(savedInstanceState?.containsKey(BUNDLE_SELECTED_INDEX) == true){
            selectedIndex = AmountUtils.stringToInt(savedInstanceState?.getString(BUNDLE_SELECTED_INDEX))
        }
        stateMachine.dispatchEvent(SettlementMainEvent.Init(selectedIndex))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettlementMainRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_settlement_dashboard))
        setupToolbar(vm)
        binding.vm = vm

        settlementMainAdapter = SettlementMainAdapter(childFragmentManager)
        binding.pager.adapter = settlementMainAdapter

        if(!movedToDefault) {
            movedToDefault = true
            stateMachine.dispatchEvent(SettlementMainEvent.UpdateSelectedState(selectedIndex, true))
        }
    }

    override fun registerListener() {
        super.registerListener()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateSelectedState(tab!!.position)
            }
        })
    }

    private fun updateSelectedState(position: Int){
        when(position){
            1 -> {stateMachine.dispatchEvent(SettlementMainEvent.UpdateSelectedState(1))}
            else -> {stateMachine.dispatchEvent(SettlementMainEvent.UpdateSelectedState(0))}
        }
    }

    override fun handleUiSideEffect(sideEffect: SettlementMainUSF) {
        when (sideEffect) {
            is SettlementMainUSF.ShowToast -> {

            }
            is SettlementMainUSF.UpdateFragment -> {
                lifecycleScope.launch {
                    delay(100)
                    binding.pager.setCurrentItem(sideEffect.selectedIndex, true)
                }
            }
        }
    }

}
