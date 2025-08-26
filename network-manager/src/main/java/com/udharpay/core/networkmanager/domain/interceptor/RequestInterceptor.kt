package com.udharpay.core.networkmanager.domain.interceptor

import com.udharpay.core.networkmanager.data.NetworkRequest

interface RequestInterceptor {
    suspend fun intercept(networkRequest: NetworkRequest): NetworkRequest
}
