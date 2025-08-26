package com.rocketpay.mandate.feature.login.data.entities

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
internal data class UserDto(
    @SerializedName("mobile_number")
    val mobileNumber: String,
    @SerializedName("new_user")
    val newUser: Boolean,
    @SerializedName("token")
    val token: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("account_id")
    val accountId: String
)
