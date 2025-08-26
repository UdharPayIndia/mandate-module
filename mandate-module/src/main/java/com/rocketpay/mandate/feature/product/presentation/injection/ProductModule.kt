package com.rocketpay.mandate.feature.product.presentation.injection

import com.rocketpay.mandate.feature.permission.common.PermissionModule
import com.rocketpay.mandate.feature.product.data.ProductRepositoryImpl
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductDataStore
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductOrderDao
import com.rocketpay.mandate.feature.product.data.datasource.local.ProductWalletDao
import com.rocketpay.mandate.feature.product.data.datasource.remote.ProductService
import com.rocketpay.mandate.feature.product.data.mapper.ProductOrderDtoTOEntMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductOrderEntToDomMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductPlanDtoToDomMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductWalletDtoToEntMapper
import com.rocketpay.mandate.feature.product.data.mapper.ProductWalletEntToDomMapper
import com.rocketpay.mandate.feature.product.domain.repositories.ProductRepository
import com.rocketpay.mandate.feature.product.domain.usecase.ProductUseCase
import com.rocketpay.mandate.feature.product.presentation.ui.order.list.adapter.ProductOrderListAdapter
import com.rocketpay.mandate.feature.property.domain.usecase.PropertyUseCase
import com.rocketpay.mandate.feature.property.presentation.injection.PropertyModule
import com.rocketpay.mandate.main.database.MandateDatabase
import dagger.Module
import dagger.Provides

@Module(includes = [PropertyModule::class, PermissionModule::class])
internal open class ProductModule {


    @Provides
    internal fun provideProductOrderListAdapter(): ProductOrderListAdapter {
        return ProductOrderListAdapter()
    }

    @Provides
    internal fun provideProductStateMachineFactory(
        productUseCase: ProductUseCase,
        propertyUseCase: PropertyUseCase,
    ): ProductStateMachineFactory {
        return ProductStateMachineFactory(productUseCase, propertyUseCase)
    }

    @Provides
    internal fun provideProductUseCase(productRepository: ProductRepository): ProductUseCase {
        return ProductUseCase(productRepository)
    }

    @Provides
    internal fun provideProductRepository(
        productService: ProductService,
        productWalletDao: ProductWalletDao,
        productOrderDao: ProductOrderDao,
        productDataStore: ProductDataStore
    ): ProductRepository {
        return ProductRepositoryImpl(
            productService,
            productWalletDao,
            productOrderDao,
            ProductWalletDtoToEntMapper(),
            ProductWalletEntToDomMapper(),
            ProductPlanDtoToDomMapper(),
            ProductOrderDtoTOEntMapper(),
            ProductOrderEntToDomMapper(),
            productDataStore
        )
    }

    @Provides
    internal fun provideProductService(): ProductService {
        return ProductService()
    }

    @Provides
    internal fun provideProductWalletDao(): ProductWalletDao {
        return MandateDatabase.instance.productWalletDao()
    }

    @Provides
    internal fun provideProductOrderDao(): ProductOrderDao {
        return MandateDatabase.instance.productOrderDao()
    }

    @Provides
    internal fun provideProductDataStore(): ProductDataStore {
        return ProductDataStore()
    }
}
