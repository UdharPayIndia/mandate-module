package com.rocketpay.mandate.feature.product.data

import com.rocketpay.mandate.common.syncmanager.client.SyncManager
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductDataStore
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductOrderDao
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductWalletDao
import com.rocketpay.mandate.feature.product.data.datasource.remote.ProductService
import com.rocketpay.mandate.feature.product.data.entities.CreateProductOrderRequest
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderDto
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderEntity
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletDto
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletEntity
import com.rocketpay.mandate.feature.product.data.mapper.ProductOrderDtoTOEntMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductOrderEntToDomMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductPlanDtoToDomMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductWalletDtoToEntMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductWalletEntToDomMapper
import com.rocketpay.mandate.feature.product.domain.entities.PaymentMethod
import com.rocketpay.mandate.feature.product.domain.entities.PaymentMethodEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrder
import com.rocketpay.mandate.feature.product.domain.entities.ProductOrderTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductPlan
import com.rocketpay.mandate.feature.product.domain.entities.ProductTypeEnum
import com.rocketpay.mandate.feature.product.domain.entities.ProductWallet
import com.rocketpay.mandate.feature.product.domain.repositories.ProductRepository
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

internal class ProductRepositoryImpl(
    private val productService: ProductService,
    private val productWalletDao: ProductWalletDao,
    private val productOrderDao: ProductOrderDao,
    private val productWalletDtoToEntMapper: ProductWalletDtoToEntMapper,
    private val productWalletEntToDomMapper: ProductWalletEntToDomMapper,
    private val productPlanDtoToDomMapper: ProductPlanDtoToDomMapper,
    private val productOrderDtoTOEntMapper: ProductOrderDtoTOEntMapper,
    private val productOrderEntToDomMapper: ProductOrderEntToDomMapper,
    private val productDataStore: ProductDataStore
): ProductRepository {

    override suspend fun getProductWallet(): Outcome<List<ProductWalletDto>>{
        return productService.getProductWallet()
    }

    override fun saveProductWallet(
        data: List<ProductWalletDto>,
        onInsert: (ProductWalletEntity) -> Unit,
        onUpdate: (ProductWalletEntity, ProductWalletEntity) -> Unit,
    ) {
        val entities = productWalletDtoToEntMapper.mapList(data)
        productWalletDao.upsert(entities, onInsert, onUpdate)
    }

    override fun getProductWallet(productType: String): Flow<ProductWallet?> {
        return productWalletDao.getOne(productType).transform {
            if(it != null){
                emit(productWalletEntToDomMapper.map(it))
            }else{
                emit(null)
            }
        }
    }

    override suspend fun getProductWalletNonLive(productType: String): ProductWallet?{
        val entity = productWalletDao.getOneNonLive(productType)
        return if(entity != null){
            productWalletEntToDomMapper.map(entity)
        }else{
            null
        }
    }

    override suspend fun getProductPlanList(productType: String, isLimited: Boolean): Outcome<List<ProductPlan>>{
        return when(val outcome = productService.getProductPlanList(productType, isLimited)){
            is Outcome.Success -> {
                Outcome.Success(productPlanDtoToDomMapper.mapList(outcome.data))
            }
            is Outcome.Error -> {
                outcome
            }
        }
    }

    override suspend fun claimFreeTrail(productType: String): Outcome<GenericErrorResponse>{
        return when(val outcome = productService.claimFreeTrail(productType)){
            is Outcome.Success -> {
                SyncManager.getInstance().enqueue(ProductWalletSyncer.Companion.TYPE)
                outcome
            }
            is Outcome.Error -> {
                outcome
            }
        }
    }


    override suspend fun createProductOrder(request: CreateProductOrderRequest): Outcome<ProductOrder>{
        return when(val outcome = productService.createProductOrder(request)){
            is Outcome.Success -> {
                val entity = productOrderDtoTOEntMapper.map(outcome.data)
                val data = productOrderEntToDomMapper.map(entity)
                productOrderDao.upsert(listOf(entity), {}, { _,_-> })
                Outcome.Success(data)
            }
            is Outcome.Error -> {
                outcome
            }
        }
    }

    override suspend fun refreshProductOrder(id: String): Outcome<ProductOrder>{
        return when(val outcome = productService.refreshProductOrder(id)){
            is Outcome.Success -> {
                val entity = productOrderDtoTOEntMapper.map(outcome.data)
                val data = productOrderEntToDomMapper.map(entity)
                productOrderDao.upsert(listOf(entity), {}, { _,_-> })
                Outcome.Success(data)
            }
            is Outcome.Error -> {
                outcome
            }
        }
    }

    override suspend fun syncProductOrder(serverSequence: Long): Outcome<List<ProductOrderDto>>{
        return productService.syncProductOrder(serverSequence)
    }

    override suspend fun lastServerSequence(): Long {
        return productOrderDao.lastServerSequence()
    }

    override suspend fun getProductOrder(productOrderId: String): Flow<ProductOrder?> {
        return productOrderDao.getOne(productOrderId).transform {
            if(it != null){
                emit(productOrderEntToDomMapper.map(it))
            }else{
                emit(null)
            }
        }

    }

    override fun saveProductOrder(
        data: List<ProductOrderDto>,
        onInsert: (ProductOrderEntity) -> Unit,
        onUpdate: (ProductOrderEntity, ProductOrderEntity) -> Unit,
    ) {
        val entities = productOrderDtoTOEntMapper.mapList(data)
        productOrderDao.upsert(entities, onInsert, onUpdate)
    }

    override fun getProductOrders(productType: String): Flow<List<ProductOrder>>{
        val list = if(productType == ProductTypeEnum.CreditScore.value){
            productOrderDao.getAllByProductType(productType)
        }else{
            productOrderDao.getAllByProductType(productType, ProductOrderTypeEnum.Redeem.value)
        }
        return list.transform {
            if(!it.isNullOrEmpty()){
                emit(productOrderEntToDomMapper.mapList(it))
            }else{
                emit(emptyList())
            }
        }
    }

    override suspend fun getPaymentMethods(): Outcome<List<PaymentMethod>>{
        return when(val result = productService.getPaymentMethods()){
            is Outcome.Success -> {
                Outcome.Success(result.data.map { PaymentMethod(PaymentMethodEnum.get(it.method), it.priority) })
            }
            is Outcome.Error -> {
                result
            }
        }
    }
}