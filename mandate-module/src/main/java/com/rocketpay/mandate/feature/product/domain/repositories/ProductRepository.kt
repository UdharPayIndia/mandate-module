package com.rocketpay.mandate.feature.product.domain.repositories

import com.rocketpay.mandate.feature.product.data.entities.CreateProductOrderRequest
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderDto
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletDto
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import com.rocketpay.mandate.feature.product.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductPlan
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow

internal interface ProductRepository {
    suspend fun getProductWallet(): Outcome<List<ProductWalletDto>>
    fun saveProductWallet(
        data: List<ProductWalletDto>,
        onInsert: (ProductWalletEntity) -> Unit,
        onUpdate: (ProductWalletEntity, ProductWalletEntity) -> Unit,
    )
    fun getProductWallet(productType: String): Flow<ProductWallet?>
    suspend fun getProductWalletNonLive(productType: String): ProductWallet?
    suspend fun getProductPlanList(productType: String, isLimited: Boolean): Outcome<List<ProductPlan>>
    suspend fun claimFreeTrail(productType: String): Outcome<GenericErrorResponse>
    suspend fun createProductOrder(request: CreateProductOrderRequest): Outcome<ProductOrder>
    suspend fun refreshProductOrder(id: String): Outcome<ProductOrder>
    suspend fun syncProductOrder(serverSequence: Long): Outcome<List<ProductOrderDto>>
    suspend fun lastServerSequence(): Long
    suspend fun getProductOrder(productOrderId: String): Flow<ProductOrder?>
    fun saveProductOrder(
        data: List<ProductOrderDto>,
        onInsert: (ProductOrderEntity) -> Unit,
        onUpdate: (ProductOrderEntity, ProductOrderEntity) -> Unit,
    )
    fun getProductOrders(productType: String): Flow<List<ProductOrder>>
    suspend fun getPaymentMethods(): Outcome<List<PaymentMethod>>

}