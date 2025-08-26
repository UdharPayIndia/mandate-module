package com.udharpay.core.networkmanager.client

import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.networkmanager.domain.entities.RequestTimeouts
import com.udharpay.core.networkmanager.domain.entities.SslCertificate
import com.udharpay.core.networkmanager.domain.interceptor.RequestInterceptor
import com.udharpay.core.networkmanager.domain.interceptor.ResponseInterceptor
import com.udharpay.kernel.kernelcommon.jsonconverter.JsonHandler
import okhttp3.Interceptor
import retrofit2.Converter
import java.lang.reflect.Type

interface NetworkManager {

    companion object {
        private lateinit var instance: NetworkManager
        fun getInstance(): NetworkManager {
            return instance
        }
    }

    class Builder {
        private lateinit var baseUrl: String
        private var requestTimeouts = RequestTimeouts()
        private var requestInterceptors = mutableListOf<Interceptor>()
        private var responseInterceptors = mutableListOf<Interceptor>()

        private var networkRequestInterceptors = mutableListOf<RequestInterceptor>()
        private var networkResponseInterceptors = mutableListOf<ResponseInterceptor>()

        private var sslCertificates = mutableListOf<SslCertificate>()
        private lateinit var jsonHandler: JsonHandler
        private lateinit var converterFactory: Converter.Factory

        fun setBaseUrl(baseUrl: String) = apply {
            this.baseUrl = baseUrl
        }

        fun setRequestTimeouts(requestTimeouts: RequestTimeouts) = apply {
            this.requestTimeouts = requestTimeouts
        }

        fun addRequestInterceptor(requestInterceptor: Interceptor) = apply {
            this.requestInterceptors.add(requestInterceptor)
        }

        fun addRequestInterceptors(requestInterceptors: List<Interceptor>) = apply {
            this.requestInterceptors.addAll(requestInterceptors)
        }

        fun addNetworkRequestInterceptor(requestInterceptor: RequestInterceptor) = apply {
            this.networkRequestInterceptors.add(requestInterceptor)
        }

        fun addNetworkRequestInterceptors(requestInterceptors: List<RequestInterceptor>) = apply {
            this.networkRequestInterceptors.addAll(requestInterceptors)
        }

        fun addResponseInterceptor(responseInterceptor: Interceptor) = apply {
            this.responseInterceptors.add(responseInterceptor)
        }

        fun addResponseInterceptors(responseInterceptors: List<Interceptor>) = apply {
            this.responseInterceptors.addAll(responseInterceptors)
        }

        fun addNetworkResponseInterceptor(responseInterceptor: ResponseInterceptor) = apply {
            this.networkResponseInterceptors.add(responseInterceptor)
        }

        fun addNetworkResponseInterceptors(responseInterceptors: List<ResponseInterceptor>) = apply {
            this.networkResponseInterceptors.addAll(responseInterceptors)
        }

        fun addSslCertificate(sslCertificate: SslCertificate) = apply {
            this.sslCertificates.add(sslCertificate)
        }

        fun addSslCertificates(sslCertificates: List<SslCertificate>) = apply {
            this.sslCertificates.addAll(sslCertificates)
        }

        fun setConverterFactory(converterFactory: Converter.Factory) = apply {
            this.converterFactory = converterFactory
        }

        fun setJsonHandler(jsonHandler: JsonHandler) = apply {
            this.jsonHandler = jsonHandler
        }

        fun build() {
            val instanceProvider = InstanceProvider(
                baseUrl,
                requestTimeouts,
                requestInterceptors,
                responseInterceptors,
                sslCertificates,
                jsonHandler,
                converterFactory,
                networkRequestInterceptors,
                networkResponseInterceptors
            )

            instance = NetworkManagerImpl(instanceProvider)
        }
    }

    suspend fun <T> get(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> post(networkRequest: NetworkRequest, type: Type) : Outcome<T>
    suspend fun <T> patch(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> put(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> delete(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> head(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> apiCall(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> getWithStream(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    suspend fun <T> postWithMultipart(networkRequest: NetworkRequest, type: Type) : Outcome<T>

    fun getBaseUrl(): String
}
