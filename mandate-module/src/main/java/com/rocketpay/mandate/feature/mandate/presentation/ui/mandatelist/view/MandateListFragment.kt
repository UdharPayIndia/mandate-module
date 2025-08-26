package com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.databinding.FragmentMandateListRpBinding
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.statemachine.BankAccountAddScreen
import com.rocketpay.mandate.feature.bankaccount.presentation.ui.bankaccountadd.view.BankAccountAddFragment
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateComponent
import com.rocketpay.mandate.feature.mandate.presentation.injection.MandateStateMachineFactory
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.statemachine.MandateAddScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandateadd.view.MandateAddFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.statemachine.MandateDetailScreen
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatedetail.view.MandateDetailFragment
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.adapter.CustomEditTextWithBackPressEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.adapter.MandateListAdapter
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListEvent
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListState
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListStateMachine
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.statemachine.MandateListUSF
import com.rocketpay.mandate.feature.mandate.presentation.ui.mandatelist.viewmodel.MandateListUM
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.bottomsheetselection.DialogBottomSheetSelection
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.KeyboardUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.statemachine.PaymentTrackerMainScreen
import com.rocketpay.mandate.feature.installment.presentation.ui.paymentSchedule.main.view.PaymentTrackerMainFragment
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryScreen
import com.rocketpay.mandate.feature.product.presentation.ui.summary.view.ProductSummaryFragment
import com.rocketpay.mandate.feature.profile.presentation.ui.statemachine.UserProfileScreen
import com.rocketpay.mandate.feature.profile.presentation.ui.view.UserProfileFragment
import javax.inject.Inject


internal class MandateListFragment : BaseMainFragment<MandateListEvent, MandateListState, MandateListUSF>() {

    private lateinit var binding: FragmentMandateListRpBinding
    @Inject lateinit var mandateListAdapter: MandateListAdapter
    private lateinit var vm: MandateListUM
    @Inject
    internal lateinit var mandateStateMachineFactory: MandateStateMachineFactory
    private var sortSelection: DialogBottomSheetSelection? = null
    private var filterSelection: DialogBottomSheetSelection? = null
    private var paymentTrackerIcon: ImageView? = null
    private var paymentTrackerRedDot: ImageView? = null

    companion object {
        fun newInstance(bundle: Bundle?): MandateListFragment {
            val mandateListFragment = MandateListFragment()
            mandateListFragment.arguments = bundle
            return mandateListFragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_mandate_list_rp, menu)
        paymentTrackerIcon = menu.findItem(R.id.action_payment_tracker).actionView?.findViewById(R.id.iv_payment_tracker)
        paymentTrackerRedDot = menu.findItem(R.id.action_payment_tracker).actionView?.findViewById(R.id.iv_red_dot)
        paymentTrackerIcon?.setOnClickListener {
            vm.onPaymentTrackerClicked()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_support ->{
                handleContactUsClick()
            }
            R.id.action_payment_tracker -> {
                vm.onPaymentTrackerClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun injection() {
        super.injection()
        MandateComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, mandateStateMachineFactory)[MandateListStateMachine::class.java]
        vm = MandateListUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        stateMachine.dispatchEvent(MandateListEvent.LoadMandates)
        stateMachine.dispatchEvent(MandateListEvent.RefreshClick)
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        super.viewCreated(view, savedInstanceState)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMandateListRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarTitleString.set(ResourceManager.getInstance().getString(R.string.rp_mandate))
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_profile_icon))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm
        mandateListAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.adapter = mandateListAdapter
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_hr),
                showFirstDivider = false,
                showLastDivider = false,
                extraPaddingLeft = ResourceManager.getInstance().getDimension(R.dimen.rp_size_72),
                extraPaddingRight = 0.toFloat(),
                dividerStartPosition = 0
            )
        )
        binding.refresh.setColorSchemeResources(R.color.rp_blue_2)
        binding.refresh.setOnRefreshListener { stateMachine.dispatchEvent(MandateListEvent.RefreshClick) }

        binding.queryET.setOnBackPressListener(object : CustomEditTextWithBackPressEvent.MyEditTextListener {
            override fun callback() {
                stateMachine.dispatchEvent(MandateListEvent.SearchCloseClick(false))
            }
        })

        val bundle = Bundle()
        bundle.putString(ProductSummaryFragment.BUNDLE_PRODUCT_NAME, ProductTypeEnum.Installment.value)
        listener?.attacheChildFragment(
            childFragmentManager,
            ProductSummaryFragment.newInstance(bundle),
            false,
            R.id.lytWallet,
            ProductSummaryScreen.name
        )
    }

    override fun registerListener() {
        super.registerListener()
        vm.isOutstandingBalanceUpdated.observe(viewLifecycleOwner, Observer {
            updateBadgeCount(it)
        })
    }

    override fun handleState(state: MandateListState) {
        vm.handleState(state)
        binding.refresh.isRefreshing = state.isRefreshing
    }

    private fun updateBadgeCount(flag: Boolean) {
        if(flag){
            paymentTrackerRedDot?.visibility = View.VISIBLE
        }else{
            paymentTrackerRedDot?.visibility = View.GONE
        }
    }

    override fun handleUiSideEffect(sideEffect: MandateListUSF) {
        when (sideEffect) {
            is MandateListUSF.GotoMandateDetail -> {
                val bundle = Bundle()
                bundle.putString(MandateDetailFragment.MANDATE_ID, sideEffect.mandate.id)
                bundle.putBoolean(MandateDetailFragment.BUNDLE_IS_MANUAL, sideEffect.isManual)
                listener?.onNavigate(MandateDetailFragment.newInstance(bundle), fragmentTag = MandateDetailScreen.name)
            }
            is MandateListUSF.GotoAddMandate -> {
                val bundle = Bundle()
                bundle.putString(MandateAddFragment.BUNDLE_REFERENCE_TYPE, "SDK")
                listener?.onNavigate(MandateAddFragment.newInstance(bundle), fragmentTag = MandateAddScreen.name)
            }
            is MandateListUSF.UpdateMandates -> {
                mandateListAdapter.swapData(sideEffect.mandates)
            }
            is MandateListUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is MandateListUSF.OpenKeyboard -> {
                binding.refresh.isEnabled = false
                binding.rvList.layoutManager?.scrollToPosition(0)
                binding.queryET.requestFocus()
                KeyboardUtils.showKeyBoard(binding.queryET, requireContext())
                binding.appBar.setExpanded(false)

            }
            is MandateListUSF.CloseKeyboard -> {
                binding.refresh.isEnabled = true
                binding.queryET.clearFocus()
                KeyboardUtils.hideKeyboard(binding.queryET, requireContext())
            }
            is MandateListUSF.ShowSortDropDown -> {
                if (sortSelection == null) {
                    sortSelection = DialogBottomSheetSelection(
                        requireContext(),
                        ResourceManager.getInstance().getString(R.string.rp_mandate_sort_by),
                        sideEffect.currentPosition.value
                    ) { itemDialogSelection ->
                        sortSelection?.dismiss()
                        stateMachine.dispatchEvent(MandateListEvent.SortSelected(itemDialogSelection.type))
                    }
                }
                sortSelection?.updateList(sideEffect.sortTypes)
                sortSelection?.show()

                sortSelection?.setOnDismissListener {
                    stateMachine.dispatchEvent(MandateListEvent.SearchCloseClick(true))
                }
            }
            is MandateListUSF.ShowFilterDropDown -> {
                if (filterSelection == null) {
                    filterSelection = DialogBottomSheetSelection(
                        requireContext(),
                        ResourceManager.getInstance().getString(R.string.rp_mandate_filter_by),
                        sideEffect.currentPosition.value
                    ) { itemDialogSelection ->
                        filterSelection?.dismiss()
                        stateMachine.dispatchEvent(MandateListEvent.FilterSelected(itemDialogSelection.type))
                    }
                }
                filterSelection?.updateList(sideEffect.sortTypes)
                filterSelection?.show()

                filterSelection?.setOnDismissListener {
                    stateMachine.dispatchEvent(MandateListEvent.SearchCloseClick(true))
                }
            }
            is MandateListUSF.RestFilter -> {
                filterSelection?.updateCurrentPosition(sideEffect.mandateSearchFilterSort.mandateFilter.value)
                sortSelection?.updateCurrentPosition(sideEffect.mandateSearchFilterSort.mandateSort.value)
            }
            is MandateListUSF.OpenBankAccountAddition -> {
                val bundle = Bundle()
                bundle.putBoolean(BankAccountAddFragment.IS_FROM_ONBOARDING, true)
                bundle.putString(BankAccountAddFragment.BUNDLE_SOURCE, sideEffect.source)
                listener?.onNavigate(BankAccountAddFragment.newInstance(bundle), fragmentTag = BankAccountAddScreen.name)
            }
            is MandateListUSF.OpenPaymentTracker -> {
                val bundle = Bundle()
                bundle.putBoolean(PaymentTrackerMainFragment.BUNDLE_IS_SUPER_KEY_FLOW, false)
                listener?.onNavigate(PaymentTrackerMainFragment.newInstance(bundle), fragmentTag = PaymentTrackerMainScreen.name)
            }
            is MandateListUSF.UserProfileClick -> {
                listener?.onNavigate(UserProfileFragment.newInstance(null), fragmentTag = UserProfileScreen.name)
            }
        }
    }

    override fun onBackPress() {
        stateMachine.dispatchEvent(MandateListEvent.UserProfileClick)
    }
}
