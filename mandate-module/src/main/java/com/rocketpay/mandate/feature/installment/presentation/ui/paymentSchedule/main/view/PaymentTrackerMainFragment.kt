package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentPaymentTrackerMainRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.adapter.PaymentTrackerAdapter
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainState
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.viewmodel.PaymentTrackerCommunicatorVM
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.viewmodel.PaymentTrackerMainUM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.statemachine.SettlementMainScreen
import com.rocketpay.mandate.feature.settlements.presentation.ui.main.view.SettlementMainFragment
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.AmountUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class PaymentTrackerMainFragment : BaseMainFragment<PaymentTrackerMainEvent, PaymentTrackerMainState, PaymentTrackerMainUSF>(){

    private lateinit var paymentTrackerAdapter: PaymentTrackerAdapter
    private lateinit var binding: FragmentPaymentTrackerMainRpBinding
    private lateinit var vm: PaymentTrackerMainUM
    private var movedToDefault = false
    private var communicatorVM: PaymentTrackerCommunicatorVM? = null
    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    var selectedIndex: Int = 1

    companion object {
        const val BUNDLE_IS_SUPER_KEY_FLOW = "is_super_key_flow"
        const val BUNDLE_SELECTED_INDEX = "index"

        fun newInstance(bundle: Bundle?): PaymentTrackerMainFragment {
            val fragment = PaymentTrackerMainFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, installmentStateMachineFactory)[PaymentTrackerMainStateMachine::class.java]
        communicatorVM = ViewModelProvider(this)[PaymentTrackerCommunicatorVM::class.java]
        vm = PaymentTrackerMainUM { stateMachine.dispatchEvent(it) }
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

    override fun handleState(state: PaymentTrackerMainState) {
        vm.handleState(state)
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        if(savedInstanceState?.containsKey(BUNDLE_SELECTED_INDEX) == true){
            selectedIndex = AmountUtils.stringToInt(savedInstanceState?.getString(BUNDLE_SELECTED_INDEX))
        }
        val isSuperKeyFlow = savedInstanceState?.getBoolean(BUNDLE_IS_SUPER_KEY_FLOW, false) ?: false
        stateMachine.dispatchEvent(PaymentTrackerMainEvent.Init(selectedIndex, isSuperKeyFlow))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPaymentTrackerMainRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_payment_tracker))
        setupToolbar(vm)
        binding.vm = vm

        paymentTrackerAdapter = PaymentTrackerAdapter(this, arguments)
        binding.lytChild.adapter = paymentTrackerAdapter

        binding.lytChild.isUserInputEnabled = true
        binding.lytChild.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateSelectedState(position)
            }
        })
        if(!movedToDefault) {
            movedToDefault = true
            stateMachine.dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(selectedIndex, true))
        }
    }

    private fun updateSelectedState(position: Int){
        when(position){
            1 -> {stateMachine.dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(1))}
            2 -> {stateMachine.dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(2))}
            else -> {stateMachine.dispatchEvent(PaymentTrackerMainEvent.UpdateSelectedState(0))}
        }
    }

    override fun handleUiSideEffect(sideEffect: PaymentTrackerMainUSF) {
        when (sideEffect) {
            is PaymentTrackerMainUSF.ShowToast -> {

            }
            is PaymentTrackerMainUSF.UpdateFragment -> {
                lifecycleScope.launch {
                    delay(100)
                    binding.lytChild.setCurrentItem(sideEffect.selectedIndex, true)
                }
            }
            is PaymentTrackerMainUSF.RefreshInstallments -> {
                communicatorVM?.refreshCollectedInstallments?.postValue(sideEffect.isCollectedUpdated)
                communicatorVM?.refreshUpcomingInstallments?.postValue(sideEffect.isUpcomingUpdated)
                communicatorVM?.refreshOutstandingInstallments?.postValue(sideEffect.isOutstandingUpdated)
            }
            is PaymentTrackerMainUSF.ViewSettlementDashBoard -> {
                listener?.onNavigate(SettlementMainFragment.newInstance(null), fragmentTag = SettlementMainScreen.name)
            }
        }
    }

}
