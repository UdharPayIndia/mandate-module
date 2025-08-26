package com.udharpay.core.networkmanager.client

import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.HttpMethod
import com.udharpay.core.networkmanager.domain.entities.Outcome
import java.lang.reflect.Type

class NetworkManagerImpl(private val instanceProvider: InstanceProvider) : NetworkManager {

    private var networkService = instanceProvider.getNetworkService()

    private var apiCallHandler = instanceProvider.getApiCallHandler()

    override suspend fun <T> get(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.get(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters
            )
        }
    }

    override suspend fun <T> post(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.post(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters,
                interceptedNetworkRequest.requestBody
            )
        }
    }

    override suspend fun <T> patch(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.patch(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters,
                interceptedNetworkRequest.requestBody
            )
        }
    }

    override suspend fun <T> put(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.put(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters,
                interceptedNetworkRequest.requestBody
            )
        }
    }

    override suspend fun <T> delete(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            if(interceptedNetworkRequest.requestBody != null) {
                networkService.delete(
                    interceptedNetworkRequest.url,
                    interceptedNetworkRequest.headers,
                    interceptedNetworkRequest.queryParameters,
                    interceptedNetworkRequest.requestBody
                )
            }else{
                networkService.delete(
                    interceptedNetworkRequest.url,
                    interceptedNetworkRequest.headers,
                    interceptedNetworkRequest.queryParameters)
            }
        }
    }

    override suspend fun <T> head(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.head(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters
            )
        }
    }

    override suspend fun <T> getWithStream(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.getWithStream(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters
            )
        }
    }

    override suspend fun <T> postWithMultipart(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return apiCallHandler.handleApiCall(type, networkRequest) { interceptedNetworkRequest ->
            networkService.postWithMultipart(
                interceptedNetworkRequest.url,
                interceptedNetworkRequest.headers,
                interceptedNetworkRequest.queryParameters,
                interceptedNetworkRequest.multipartBodies
            )
        }
    }

    override suspend fun <T> apiCall(networkRequest: NetworkRequest, type: Type) : Outcome<T> {
        return when(networkRequest.httpMethod) {
            HttpMethod.Get -> get(networkRequest, type)
            HttpMethod.Post -> post(networkRequest, type)
            HttpMethod.Patch -> patch(networkRequest, type)
            HttpMethod.Put -> put(networkRequest, type)
            HttpMethod.Delete -> delete(networkRequest, type)
            HttpMethod.Head -> head(networkRequest, type)
            else -> throw IllegalArgumentException("httpMethod cannot be null if you use apiCall function")
        }
    }

    override fun getBaseUrl(): String {
        return instanceProvider.getBaseUrl()
    }
}
