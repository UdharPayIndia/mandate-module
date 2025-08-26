package com.rocketpay.mandate.feature.settlements.presentation.ui.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentSettlementDetailRpBinding
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementStateMachineFactory
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.adapter.SettledInstallmentListAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailState
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailUSF
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.viewmodel.SettlementDetailUM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShareUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class SettlementDetailFragment  : BaseMainFragment<SettlementDetailEvent, SettlementDetailState, SettlementDetailUSF>(){

    private lateinit var binding: FragmentSettlementDetailRpBinding
    private lateinit var vm: SettlementDetailUM
    @Inject
    internal lateinit var settlementStateMachineFactory: SettlementStateMachineFactory
    @Inject
    internal lateinit var adapter: SettledInstallmentListAdapter

    companion object {
        const val BUNDLE_SETTLEMENT_ID = "settlement_id"
        const val BUNDLE_PAY_IN_ORDER_ID = "pay_in_order_id"
        fun newInstance(bundle: Bundle?): SettlementDetailFragment {
            val fragment = SettlementDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        SettlementComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, settlementStateMachineFactory)[SettlementDetailStateMachine::class.java]
        vm = SettlementDetailUM { stateMachine.dispatchEvent(it) }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettlementDetailRpBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val settlementId = savedInstanceState?.getString(BUNDLE_SETTLEMENT_ID).orEmpty()
        val payInOrderId = savedInstanceState?.getString(BUNDLE_PAY_IN_ORDER_ID).orEmpty()
        stateMachine.dispatchEvent(SettlementDetailEvent.Init(settlementId = settlementId, payInOrderId = payInOrderId))
    }

    override fun handleState(state: SettlementDetailState) {
        vm.handleState(state)
        listener?.updateStatusBar(ResourceManager.getInstance().getColor(R.color.rp_green_1))
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_green_2))
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_settlement_details))
        setupToolbar(vm)
        binding.vm = vm

        adapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_hr_thin),
                showFirstDivider = false,
                showLastDivider = false,
                extraPaddingLeft = 0.toFloat(),
                extraPaddingRight = 0.toFloat(),
                dividerStartPosition = 0
            )
        )
    }

    override fun handleUiSideEffect(sideEffect: SettlementDetailUSF) {
        when(sideEffect){
            is SettlementDetailUSF.Copy -> {
                ShareUtils.copyToClipboard(requireContext(), sideEffect.link)
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is SettlementDetailUSF.SetInstallments -> {
                adapter.swapData(sideEffect.installments, sideEffect.refundedInstallments)
            }
            is SettlementDetailUSF.OpenMandate -> {
                val bundle = Bundle()
                bundle.putString(MandateDetailFragment.MANDATE_ID, sideEffect.installment.mandateId)
                bundle.putInt(MandateDetailFragment.BUNDLE_INSTALLMENT_SERIAL_NUMBER, sideEffect.installment.serialNumber)
                listener?.onNavigate(MandateDetailFragment.newInstance(bundle), fragmentTag = MandateDetailScreen.name)
            }
            is SettlementDetailUSF.CloseScreen -> {
                listener?.onBackPressed()
            }
        }
    }

    override fun deInitView() {
        super.deInitView()
        listener?.updateStatusBar(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }

}
