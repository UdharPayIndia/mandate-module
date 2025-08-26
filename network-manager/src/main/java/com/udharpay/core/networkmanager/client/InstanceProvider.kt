package com.udharpay.core.networkmanager.client

import com.udharpay.core.networkmanager.data.NetworkService
import com.udharpay.core.networkmanager.domain.entities.RequestTimeouts
import com.udharpay.core.networkmanager.domain.entities.SslCertificate
import com.udharpay.core.networkmanager.domain.interceptor.RequestInterceptor
import com.udharpay.core.networkmanager.domain.interceptor.ResponseInterceptor
import com.udharpay.core.networkmanager.domain.usecase.ApiCallHandler
import com.udharpay.core.networkmanager.domain.usecase.SslPinner
import com.udharpay.kernel.kernelcommon.jsonconverter.JsonHandler
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class InstanceProvider(
    private val baseUrl: String,
    private val requestTimeouts: RequestTimeouts,
    private val requestInterceptors: List<Interceptor>,
    private val responseInterceptors: List<Interceptor>,
    private val sslCertificates: List<SslCertificate>,
    private val jsonHandler: JsonHandler,
    private val converterFactory: Converter.Factory,
    private val networkRequestInterceptors: MutableList<RequestInterceptor>,
    private val networkResponseInterceptors: MutableList<ResponseInterceptor>
) {
    fun getNetworkService(): NetworkService {
        return getRetrofit(
            getOkHttpClient(
                requestInterceptors,
                responseInterceptors,
                requestTimeouts,
                sslCertificates
            ),
            baseUrl,
            converterFactory
        ).create(NetworkService::class.java)
    }

    private fun getRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    private fun getOkHttpClient(
        requestInterceptors: List<Interceptor>,
        responseInterceptors: List<Interceptor>,
        requestTimeouts: RequestTimeouts,
        sslCertificates: List<SslCertificate>
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(requestTimeouts.connectTimeout, requestTimeouts.unit)
            .readTimeout(requestTimeouts.readTimeout, requestTimeouts.unit)
            .writeTimeout(requestTimeouts.writeTimeout, requestTimeouts.unit)

        // request interceptor
        requestInterceptors.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        // response interceptor
        responseInterceptors.forEach {
            okHttpClientBuilder.addInterceptor(it)
        }

        // ssl pinning
        val sslPinner = SslPinner()
        sslPinner.pin(okHttpClientBuilder, sslCertificates)

        return okHttpClientBuilder.build()
    }

    fun getApiCallHandler(): ApiCallHandler {
        return ApiCallHandler(jsonHandler, networkRequestInterceptors, networkResponseInterceptors)
    }

    fun getBaseUrl(): String {
        return baseUrl
    }
}
