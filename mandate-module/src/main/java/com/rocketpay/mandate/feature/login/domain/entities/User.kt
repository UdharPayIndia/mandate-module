package com.rocketpay.mandate.feature.login.domain.entities

internal data class User(
    val mobileNumber: String,
    val newUser: Boolean,
    val token: String,
    val userId: String,
    val accountId: String
)
