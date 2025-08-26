package com.rocketpay.mandate.main.init.network

import android.os.Build
import com.rocketpay.mandate.feature.login.data.datasource.local.LoginDataStore
import com.rocketpay.mandate.BuildConfig
import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.domain.interceptor.RequestInterceptor

internal class TokenRequestInterceptor(private val userDataStore: LoginDataStore) : RequestInterceptor {

    override suspend fun intercept(networkRequest: NetworkRequest): NetworkRequest {
        val filterUrl = networkRequest.url
            .replace("///", "/")
            .replace("//", "/")
            .replace("https:/", "https://")
            .replace("http:/", "http://")

        return NetworkRequestBuilder(networkRequest)
            .header("x-token", userDataStore.getToken())
            .header("x-platform", "android")
            .header("x-platform-version", Build.VERSION.SDK_INT.toString())
            .header("x-app", "udharpay")
            .header("x-app-context", "SDK_APP")
            .header("x-app-version", BuildConfig.VERSION_CODE.toString())
            .url(filterUrl)
            .build()
    }
}
