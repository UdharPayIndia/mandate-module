package com.rocketpay.mandate.feature.product.presentation.ui.summary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.rocketpay.mandate.common.basemodule.common.presentation.progressdialog.ProgressDialog
import com.rocketpay.mandate.common.basemodule.statemachine.view.StateMachineBottomSheetFragment
import com.rocketpay.mandate.databinding.FragmentProductSummaryRpBinding
import com.rocketpay.mandate.feature.product.presentation.injection.ProductComponent
import com.rocketpay.mandate.feature.product.presentation.injection.ProductStateMachineFactory
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.statemachine.ProductOrderListScreen
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.view.ProductOrderListFragment
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryEvent
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummarySimpleStateMachine
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryState
import com.rocketpay.mandate.feature.product.presentation.ui.summary.statemachine.ProductSummaryUSF
import com.rocketpay.mandate.feature.product.presentation.ui.summary.viewmodel.ProductSummaryUM
import javax.inject.Inject

internal class ProductSummaryFragment: StateMachineBottomSheetFragment<ProductSummaryEvent, ProductSummaryState, ProductSummaryUSF>(){

    private lateinit var binding: FragmentProductSummaryRpBinding
    private lateinit var vm: ProductSummaryUM
    @Inject
    internal lateinit var productStateMachineFactory: ProductStateMachineFactory

    private val progressDialog by lazy { ProgressDialog(requireContext(), vm.progressDialogVM) }

    companion object {
        const val BUNDLE_PRODUCT_NAME = "BUNDLE_PRODUCT_NAME"
        fun newInstance(bundle: Bundle?): ProductSummaryFragment {
            val fragment = ProductSummaryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun injection() {
        super.injection()
        ProductComponent.Initializer.init().inject(this)
        stateMachine = ViewModelProvider(this, productStateMachineFactory)[ProductSummarySimpleStateMachine::class.java]
        vm = ProductSummaryUM { stateMachine.dispatchEvent(it) }
    }

    override fun loadData(savedInstanceState: Bundle?) {
        super.loadData(savedInstanceState)
        val productType = savedInstanceState?.getString(BUNDLE_PRODUCT_NAME) ?: ""
        stateMachine.dispatchEvent(ProductSummaryEvent.LoadProductWallet(productType))
        stateMachine.dispatchEvent(ProductSummaryEvent.FetchProductWallet)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProductSummaryRpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.vm = vm
    }
    
    override fun handleState(state: ProductSummaryState) {
        vm.handleState(state)
    }

    override fun handleUiSideEffect(sideEffect: ProductSummaryUSF) {
        when(sideEffect){
            is ProductSummaryUSF.OpenHistoryPage -> {
                val bundle = Bundle()
                bundle.putString(ProductOrderListFragment.BUNDLE_PRODUCT_TYPE, sideEffect.productType)
                listener?.onNavigate(ProductOrderListFragment.newInstance(bundle), fragmentTag = ProductOrderListScreen.name)
            }
            is ProductSummaryUSF.ShowToast -> {
                progressDialog.dismiss()
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                    sideEffect.message, Snackbar.LENGTH_SHORT).show()
            }
            is ProductSummaryUSF.CloseLoading -> {
                progressDialog.dismiss()
            }
            is ProductSummaryUSF.ShowLoading -> {
                vm.progressDialogVM.setProgressState(sideEffect.header, sideEffect.message)
                progressDialog.show()
            }
        }
    }
}
