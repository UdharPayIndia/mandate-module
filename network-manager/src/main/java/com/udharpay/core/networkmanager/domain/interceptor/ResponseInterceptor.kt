package com.udharpay.core.networkmanager.domain.interceptor

import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.Outcome

interface ResponseInterceptor {
    suspend fun <T> intercept(outcome: Outcome<T>, networkRequest: NetworkRequest): Outcome<T>
}
