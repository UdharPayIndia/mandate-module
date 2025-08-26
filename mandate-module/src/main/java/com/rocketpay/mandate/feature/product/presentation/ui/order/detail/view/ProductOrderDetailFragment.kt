package com.rocketpay.mandate.feature.product.presentation.ui.order.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.lifecycle.ViewModelProvider
import com.rocketpay.mandate.R
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShareUtils
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.ShowUtils
import com.rocketpay.mandate.common.basemodule.main.view.BaseMainFragment
import com.rocketpay.mandate.common.resourcemanager.ResourceManager
import com.rocketpay.mandate.databinding.FragmentProductOrderDetailRpBinding
import com.rocketpay.mandate.feature.product.presentation.injection.ProductComponent
import com.rocketpay.mandate.feature.product.presentation.injection.ProductStateMachineFactory
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailEvent
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailSimpleStateMachine
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailState
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.statemachine.ProductOrderDetailUSF
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.viewmodel.ProductOrderDetailUM
import javax.inject.Inject

internal class ProductOrderDetailFragment : BaseMainFragment<ProductOrderDetailEvent, ProductOrderDetailState, ProductOrderDetailUSF>() {

    private lateinit var binding: FragmentProductOrderDetailRpBinding
    private lateinit var vm: ProductOrderDetailUM
    @Inject
    internal lateinit var productStateMachineFactory: ProductStateMachineFactory
    private var refreshIcon: ImageView? = null

    companion object {
        const val BUNDLE_PRODUCT_ORDER_ID = "product_order_id"
        const val BUNDLE_PRODUCT_TYPE = "productType"

        fun newInstance(bundle: Bundle?): ProductOrderDetailFragment {
            val fragment = ProductOrderDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_order_details_rp, menu)
        refreshIcon = menu.findItem(R.id.action_refresh).actionView?.findViewById(R.id.iv_refresh)
        refreshIcon?.setOnClickListener {
            vm.onActionRefreshClick()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.action_refresh).isVisible = vm.refreshVisibility.get()
    }

    override fun injection() {
        super.injection()
        ProductComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, productStateMachineFactory)[ProductOrderDetailSimpleStateMachine::class.java]
        vm = ProductOrderDetailUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val orderId = savedInstanceState?.getString(BUNDLE_PRODUCT_ORDER_ID) ?: ""
        val productType = savedInstanceState?.getString(BUNDLE_PRODUCT_TYPE) ?: ""
        stateMachine.dispatchEvent(ProductOrderDetailEvent.LoadProductOrder(orderId, productType))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProductOrderDetailRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        vm.toolbarIcon.set(ResourceManager.getInstance().getDrawable(R.drawable.rp_ic_back))
        vm.titleTextColor.set(ResourceManager.getInstance().getColor(R.color.rp_grey_6))
        vm.toolbarBackground.set(ResourceManager.getInstance().getDrawable(R.color.rp_blue_2))
        setupToolbar(vm)
        binding.vm = vm
    }

    override fun registerListener() {
        super.registerListener()
        vm.refreshVisibility.addOnPropertyChangedCallback(object : OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                requireActivity().invalidateOptionsMenu()
            }
        })
    }

    override fun handleState(state: ProductOrderDetailState) {
        vm.handleState(state)
        if (vm.stateColor.get() != 0) {
            listener?.updateStatusBar(ResourceManager.getInstance().getColor(vm.stateColor.get()))
        }
    }

    override fun handleUiSideEffect(sideEffect: ProductOrderDetailUSF) {
        when (sideEffect) {
            is ProductOrderDetailUSF.ShowToast -> {
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
            is ProductOrderDetailUSF.Copy -> {
                ShareUtils.copyToClipboard(requireContext(), sideEffect.link)
                ShowUtils.shortToast(requireContext(), sideEffect.message)
            }
        }
    }

    override fun deInitView() {
        super.deInitView()
        listener?.updateStatusBar(ResourceManager.getInstance().getColor(R.color.rp_blue_1))
    }
}
