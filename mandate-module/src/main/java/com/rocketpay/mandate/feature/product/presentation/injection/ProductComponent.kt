package com.rocketpay.mandate.feature.product.presentation.injection

import com.rocketpay.mandate.feature.product.data.ProductOrderSyncer
import com.rocketpay.mandate.feature.product.data.ProductWalletSyncer
import com.rocketpay.mandate.feature.product.presentation.ui.order.detail.view.ProductOrderDetailFragment
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.view.ProductOrderListFragment
import com.rocketpay.mandate.feature.product.presentation.ui.summary.view.ProductSummaryFragment
import dagger.Component

@Component(modules = [ProductModule::class])
internal interface ProductComponent {

    fun inject(productSummaryFragment: ProductSummaryFragment)
    fun inject(productOrderListFragment: ProductOrderListFragment)
    fun inject(productOrderDetailFragment: ProductOrderDetailFragment)
    fun inject(productWalletSyncer: ProductWalletSyncer)
    fun inject(productOrderSyncer: ProductOrderSyncer)

    object Initializer {
        fun init(): ProductComponent {
            return DaggerProductComponent.builder().build()
        }
    }
}
