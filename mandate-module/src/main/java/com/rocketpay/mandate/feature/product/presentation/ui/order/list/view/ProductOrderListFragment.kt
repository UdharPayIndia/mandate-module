package com.rocketpay.mandate.feature.product.presentation.ui.order.list.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.adapter.DividerItemDecoration
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentProductOrderListRpBinding
import com.rocketpay.mandate.feature.product.presentation.injection.ProductComponent
import com.rocketpay.mandate.feature.product.presentation.injection.ProductStateMachineFactory
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.view.ProductOrderDetailFragment
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.adapter.ProductOrderListAdapter
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListSimpleStateMachine
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListState
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListUSF
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.viewmodel.ProductOrderListUM
import javax.inject.Inject

internal class ProductOrderListFragment: BaseMainFragment<ProductOrderListEvent, ProductOrderListState, ProductOrderListUSF>() {

    private lateinit var binding: FragmentProductOrderListRpBinding
    @Inject
    internal lateinit var productOrderListAdapter: ProductOrderListAdapter
    private lateinit var vm: ProductOrderListUM
    @Inject
    internal lateinit var productStateMachineFactory: ProductStateMachineFactory

    companion object {
        const val BUNDLE_PRODUCT_TYPE = "BUNDLE_PRODUCT_TYPE"
        fun newInstance(bundle: Bundle?): ProductOrderListFragment {
            val fragment = ProductOrderListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        ProductComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, productStateMachineFactory)[ProductOrderListSimpleStateMachine::class.java]
        vm = ProductOrderListUM { stateMachine.dispatchEvent(it) }
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
        val productType = savedInstanceState?.getString(BUNDLE_PRODUCT_TYPE) ?: ""
        stateMachine.dispatchEvent(ProductOrderListEvent.LoadProductOrders(productType))
                stateMachine.dispatchEvent(ProductOrderListEvent.LoadProductWallet(productType))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProductOrderListRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm
        productOrderListAdapter.itemClick = { stateMachine.dispatchEvent(it) }
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                ResourceManager.getInstance().getDrawable(R.drawable.rp_divider_hr),
                showFirstDivider = false,
                showLastDivider = true,
                extraPaddingLeft = ResourceManager.getInstance().getDimension(R.dimen.rp_size_72),
                extraPaddingRight = 0.toFloat(),
                dividerStartPosition = 0
            )
        )
        binding.rvList.adapter = productOrderListAdapter

        binding.refresh.setColorSchemeResources(R.color.rp_blue_2)
        binding.refresh.setOnRefreshListener { stateMachine.dispatchEvent(ProductOrderListEvent.RefreshClick) }

    }

    override fun handleState(state: ProductOrderListState) {
        vm.handleState(state)
        binding.refresh.isRefreshing = state.isRefreshing
    }

    override fun handleUiSideEffect(sideEffect: ProductOrderListUSF) {
        when(sideEffect){
            is ProductOrderListUSF.UpdateProductOrders -> {
                productOrderListAdapter.swapData(sideEffect.productOrders, sideEffect.productType)
            }
            is ProductOrderListUSF.CloseScreen -> {
                listener?.onBackPressed()
            }
            is ProductOrderListUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is ProductOrderListUSF.OpenProductOrderDetails -> {
                val bundle = Bundle()
                bundle.putString(ProductOrderDetailFragment.BUNDLE_PRODUCT_ORDER_ID, sideEffect.productOrder.id)
                bundle.putString(ProductOrderDetailFragment.BUNDLE_PRODUCT_TYPE, sideEffect.productType)
                listener?.onNavigate(ProductOrderDetailFragment.newInstance(bundle), fragmentTag = tag ?: "")
            }
        }
    }
}
