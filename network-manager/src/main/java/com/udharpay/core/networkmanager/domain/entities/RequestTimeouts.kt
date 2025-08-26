package com.udharpay.core.networkmanager.domain.entities

import java.util.concurrent.TimeUnit

data class RequestTimeouts(
    val connectTimeout: Long = 90L,
    val readTimeout: Long = 45L,
    val writeTimeout: Long = 45L,
    val unit: TimeUnit = TimeUnit.SECONDS
)
