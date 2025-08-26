package com.rocketpay.mandate.feature.product.data.datasource.remote

import com.rocketpay.mandate.feature.product.data.entities.ClaimFreeTrailRequest
import com.rocketpay.mandate.feature.product.data.entities.CreateProductOrderRequest
import com.rocketpay.mandate.feature.product.data.entities.PaymentMethodDto
import com.rocketpay.mandate.feature.product.data.entities.ProductOrderDto
import com.rocketpay.mandate.feature.product.data.entities.ProductPlanDto
import com.rocketpay.mandate.feature.product.data.entities.ProductWalletDto
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.get
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class ProductService {

    suspend fun getProductWallet(): Outcome<List<ProductWalletDto>> {
        return NetworkRequestBuilder()
            .subUrl("/common/wallets/v2")
            .body(Any())
            .build()
            .get()
    }

    suspend fun getProductPlanList(productType: String, isLimited: Boolean): Outcome<List<ProductPlanDto>> {
        return NetworkRequestBuilder()
            .subUrl("common/product-orders/v1/products/v1")
            .queryParameter("product_type", productType)
            .queryParameter("tags", if(isLimited){
                "limited"
            }else{
                ""
            })
            .build()
            .get()
    }

    suspend fun claimFreeTrail(productType: String): Outcome<GenericErrorResponse> {
        return NetworkRequestBuilder()
            .subUrl("common/product-orders/v2/claim-free-trial")
            .body(ClaimFreeTrailRequest(productType))
            .build()
            .post()
    }


    suspend fun createProductOrder(request: CreateProductOrderRequest): Outcome<ProductOrderDto> {
        return NetworkRequestBuilder()
            .subUrl("common/product-orders/v2")
            .body(request)
            .build()
            .post()
    }

    suspend fun syncProductOrder(serverSequence: Long): Outcome<List<ProductOrderDto>> {
        return NetworkRequestBuilder()
            .subUrl("common/product-orders/v2/sync")
            .queryParameter("sync_sequence", serverSequence.toString())
            .queryParameter("limit", "10")
            .build()
            .get()
    }

    suspend fun refreshProductOrder(id: String): Outcome<ProductOrderDto> {
        return NetworkRequestBuilder()
            .subUrl("common/product-orders/v1/{id}/refresh")
            .pathParameter("id", id)
            .body(Any())
            .build()
            .post()
    }

    suspend fun getPaymentMethods(): Outcome<List<PaymentMethodDto>> {
        return NetworkRequestBuilder()
            .subUrl("payments-and-billing/lock-key/purchase/v1/methods")
            .build()
            .get()
    }

}