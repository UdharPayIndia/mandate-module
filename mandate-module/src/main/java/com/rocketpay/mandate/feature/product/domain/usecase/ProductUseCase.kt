package com.rocketpay.mandate.feature.product.domain.usecase

import com.rocketpay.mandate.feature.product.data.entities.CreateProductOrderRequest
import com.rocketpay.mandate.feature.product.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductPlan
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet
import com.rocketpay.mandate.feature.product.domain.repositories.ProductRepository
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class ProductUseCase internal constructor(
    private val productRepository: ProductRepository
) {

    fun getProductWallet(productType: String): Flow<ProductWallet?>{
        return productRepository.getProductWallet(productType)
    }

    suspend fun getProductWalletNonLive(productType: String): ProductWallet?{
        return withContext(Dispatchers.IO){ productRepository.getProductWalletNonLive(productType) }
    }

    suspend fun getProductPlanList(productType: String, isLimited: Boolean): Outcome<List<ProductPlan>>{
        return productRepository.getProductPlanList(productType, isLimited)
    }

    suspend fun claimFreeTrail(productType: String): Outcome<GenericErrorResponse>{
        return productRepository.claimFreeTrail(productType)
    }

    suspend fun createProductOrder(request: CreateProductOrderRequest): Outcome<ProductOrder>{
        return productRepository.createProductOrder(request)
    }

    suspend fun refreshProductOrder(id: String): Outcome<ProductOrder>{
        return productRepository.refreshProductOrder(id)
    }

    fun getProductOrders(productType: String): Flow<List<ProductOrder>>{
        return productRepository.getProductOrders(productType)
    }

    suspend fun getProductOrder(productOrderId: String): Flow<ProductOrder?>{
        return productRepository.getProductOrder(productOrderId)
    }

    suspend fun getPaymentMethods(): Outcome<List<PaymentMethod>>{
        return productRepository.getPaymentMethods()
    }
}