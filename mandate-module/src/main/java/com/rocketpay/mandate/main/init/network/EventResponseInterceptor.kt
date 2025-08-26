package com.rocketpay.mandate.main.init.network

import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.networkmanager.domain.interceptor.ResponseInterceptor

internal class EventResponseInterceptor: ResponseInterceptor {

    companion object {
        private const val EVENT_API_CALL_FAILED = "api_call_failed"
    }

    override suspend fun <T> intercept(outcome: Outcome<T>, networkRequest: NetworkRequest): Outcome<T> {
        if (outcome is Outcome.Error) {
            val properties = hashMapOf<String, Any>()
            properties["code"] = outcome.error.code.orEmpty()
            properties["message"] = outcome.error.message.orEmpty()
            properties["status"] = outcome.error.status ?: 0
            properties["url"] = networkRequest.url.substringBefore("?")
            properties["http_method"] = networkRequest.httpMethod?.value.toString()
        }
        return outcome
    }
}
