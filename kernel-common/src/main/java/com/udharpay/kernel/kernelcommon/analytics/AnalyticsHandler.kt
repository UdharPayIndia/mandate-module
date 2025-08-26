package com.udharpay.kernel.kernelcommon.analytics

interface AnalyticsHandler {

    fun sendEvent(eventName: String, eventProperties: Map<String, Any?>? = null)

    fun setUserProperties(properties: Map<String, Any?>)
}
