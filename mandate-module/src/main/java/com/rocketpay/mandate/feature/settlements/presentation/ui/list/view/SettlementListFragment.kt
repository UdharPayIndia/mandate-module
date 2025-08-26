package com.rocketpay.mandate.feature.settlements.presentation.ui.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentSettlementListRpBinding
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.statemachine.KycScreen
import com.rocketpay.mandate.feature.kyc.presentation.ui.kyc.view.KycFragment
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementComponent
import com.rocketpay.mandate.feature.settlements.presentation.injection.SettlementStateMachineFactory
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.statemachine.SettlementDetailScreen
import com.rocketpay.mandate.feature.settlements.presentation.ui.detail.view.SettlementDetailFragment
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.adapter.SettlementListAdapter
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListEvent
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListState
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListStateMachine
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.statemachine.SettlementListUSF
import com.rocketpay.mandate.feature.settlements.presentation.ui.list.viewmodel.SettlementListUM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class SettlementListFragment : BaseMainFragment<SettlementListEvent, SettlementListState, SettlementListUSF>(){

    private lateinit var binding: FragmentSettlementListRpBinding
    @Inject
    internal lateinit var settlementListAdapter: SettlementListAdapter
    private lateinit var vm: SettlementListUM
    @Inject
    internal lateinit var settlementStateMachineFactory: SettlementStateMachineFactory

    companion object {
        fun newInstance(bundle: Bundle?): SettlementListFragment {
            val fragment = SettlementListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        SettlementComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, settlementStateMachineFactory)[SettlementListStateMachine::class.java]
        vm = SettlementListUM { stateMachine.dispatchEvent(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettlementListRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(SettlementListEvent.LoadOutstandingSettlementBalance)
        stateMachine.dispatchEvent(SettlementListEvent.LoadSettlements(orderByDesc = true))
        stateMachine.dispatchEvent(SettlementListEvent.LoadSettlementBannerInfo)
        stateMachine.dispatchEvent(SettlementListEvent.Init)
    }

    override fun handleState(state: SettlementListState) {
        vm.handleState(state)
        binding.refresh.isRefreshing = state.isRefreshing
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
        settlementListAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = settlementListAdapter
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
        binding.rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!vm.isLoading && !vm.isLastPage) {
                    val lastVisible = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (lastVisible >= vm.settlementSize - 5) {
                        stateMachine.dispatchEvent(SettlementListEvent.FetchNextSettlements)
                    }
                }
            }
        })

        binding.refresh.setColorSchemeResources(R.color.rp_blue_2)
        binding.refresh.setOnRefreshListener { stateMachine.dispatchEvent(SettlementListEvent.RefreshClick) }

    }

    override fun handleUiSideEffect(sideEffect: SettlementListUSF) {
        when (sideEffect) {
            is SettlementListUSF.UpdateSettlements -> {
                settlementListAdapter.swapData(
                    sideEffect.paymentOrders,
                    sideEffect.isLastPage)
            }
            is SettlementListUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is SettlementListUSF.OpenSettlementDetails -> {
                val bundle = Bundle()
                bundle.putString(SettlementDetailFragment.BUNDLE_SETTLEMENT_ID, sideEffect.settlementId)
                listener?.onNavigate(SettlementDetailFragment.newInstance(bundle), fragmentTag = SettlementDetailScreen.name)
            }
            is SettlementListUSF.OpenKyc -> {
                listener?.onNavigate(
                    KycFragment.newInstance(null),
                    fragmentTag = KycScreen.name
                )
            }
            is SettlementListUSF.OpenBankAccount -> {
                val bundle = Bundle()
                bundle.putBoolean(BankAccountAddFragment.IS_FROM_ONBOARDING, false)
                bundle.putString(BankAccountAddFragment.BUNDLE_SOURCE, "settlement_banner")
                listener?.onNavigate(
                    BankAccountAddFragment.newInstance(bundle),
                    fragmentTag = BankAccountAddScreen.name
                )
            }
        }
    }

}
