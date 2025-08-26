package com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentPaymentTrackerListRpBinding
import com.rocketpay.mandate.feature.installment.presentation.injection.InstallmentStateMachineFactory
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.adapter.InstallmentListAdapter
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListEvent
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListState
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListStateMachine
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.statemachine.PaymentTrackerListUSF
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerListUM
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.list.viewmodel.PaymentTrackerType
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.viewmodel.PaymentTrackerCommunicatorVM
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import javax.inject.Inject

internal class PaymentTrackerListFragment : BaseMainFragment<PaymentTrackerListEvent, PaymentTrackerListState, PaymentTrackerListUSF>(){

    private lateinit var binding: FragmentPaymentTrackerListRpBinding
    @Inject
    internal lateinit var installmentListAdapter: InstallmentListAdapter
    private lateinit var vm: PaymentTrackerListUM
    @Inject
    internal lateinit var installmentStateMachineFactory: InstallmentStateMachineFactory
    private var communicatorVM: PaymentTrackerCommunicatorVM? = null

    companion object {
        const val BUNDLE_PAYMENT_TRACKER_TYPE = "BUNDLE_PAYMENT_TRACKER_TYPE"
        const val BUNDLE_ORDER_BY_DESC = "BUNDLE_ORDER_BY_DESC"
        const val BUNDLE_IS_SUPER_KEY_FLOW = "is_super_key_flow"
        const val BUNDLE_SKIP_MANUAL_MANDATE = "skip_manual_mandate"
        fun newInstance(bundle: Bundle?): PaymentTrackerListFragment {
            val fragment = PaymentTrackerListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, installmentStateMachineFactory)[PaymentTrackerListStateMachine::class.java]
        communicatorVM = ViewModelProvider(requireParentFragment())[PaymentTrackerCommunicatorVM::class.java]
        vm = PaymentTrackerListUM { stateMachine.dispatchEvent(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPaymentTrackerListRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val paymentTrackerType = savedInstanceState?.getString(BUNDLE_PAYMENT_TRACKER_TYPE)
        val orderByDesc = savedInstanceState?.getBoolean(BUNDLE_ORDER_BY_DESC) ?: false
        val isSuperKeyFlow = savedInstanceState?.getBoolean(BUNDLE_IS_SUPER_KEY_FLOW) ?: false
        val skipManualMandate = savedInstanceState?.getBoolean(BUNDLE_SKIP_MANUAL_MANDATE) ?: false
        stateMachine.dispatchEvent(PaymentTrackerListEvent.LoadInstallments(paymentTrackerType, orderByDesc, isSuperKeyFlow, skipManualMandate))
    }

    override fun handleState(state: PaymentTrackerListState) {
        vm.handleState(state)
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
        installmentListAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = installmentListAdapter
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
                    if (lastVisible >= vm.installmentsSize - 5) {
                        stateMachine.dispatchEvent(PaymentTrackerListEvent.FetchNextInstallments)
                    }
                }
            }

        })
    }

    override fun registerListener() {
        super.registerListener()
        communicatorVM?.refreshOutstandingInstallments?.observe(viewLifecycleOwner, Observer {
            if(it && vm.paymentTrackerType == PaymentTrackerType.Outstanding){
                stateMachine.dispatchEvent(PaymentTrackerListEvent.RefreshInstallments)
                communicatorVM?.refreshOutstandingInstallments?.postValue(false)
            }
        })
        communicatorVM?.refreshUpcomingInstallments?.observe(viewLifecycleOwner, Observer {
            if(it && vm.paymentTrackerType == PaymentTrackerType.Upcoming){
                stateMachine.dispatchEvent(PaymentTrackerListEvent.RefreshInstallments)
                communicatorVM?.refreshUpcomingInstallments?.postValue(false)
            }
        })
        communicatorVM?.refreshCollectedInstallments?.observe(viewLifecycleOwner, Observer {
            if(it && vm.paymentTrackerType == PaymentTrackerType.Collected){
                stateMachine.dispatchEvent(PaymentTrackerListEvent.RefreshInstallments)
                communicatorVM?.refreshCollectedInstallments?.postValue(false)
            }
        })
    }

    override fun handleUiSideEffect(sideEffect: PaymentTrackerListUSF) {
        when (sideEffect) {
            is PaymentTrackerListUSF.UpdateInstallments -> {
                installmentListAdapter.swapData(
                    sideEffect.installments,
                    sideEffect.isLastPage,
                    sideEffect.hideTag)
            }
            is PaymentTrackerListUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is PaymentTrackerListUSF.OpenMandateDetails -> {
                val bundle = Bundle()
                bundle.putString(MandateDetailFragment.MANDATE_ID, sideEffect.mandateId)
                bundle.putInt(MandateDetailFragment.BUNDLE_INSTALLMENT_SERIAL_NUMBER, sideEffect.installmentSerialNumber)
                listener?.onNavigate(MandateDetailFragment.newInstance(bundle), fragmentTag = MandateDetailScreen.name)
            }
        }
    }

}
