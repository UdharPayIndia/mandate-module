package com.rocketpay.mandate.feature.login.domain.repositories

import com.rocketpay.mandate.feature.login.data.entities.CreateDeviceRequest
import com.rocketpay.mandate.feature.login.data.entities.DeviceResponseDto
import com.rocketpay.mandate.feature.login.domain.entities.User
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal interface LoginRepository {
    fun getMobileNumber(): String
    suspend fun requestOtp(mobileNumber: String, appSignature: String): Outcome<Boolean>
    suspend fun verifyOtp(mobileNumber: String, enterpriseId: String,
                          otp: String, deviceId: String): Outcome<User>
    suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Outcome<DeviceResponseDto>
    fun getUserId(): String
    fun getAccountId(): String
    fun getCountryCode(): String
    fun setName(name: String)
    fun getName(): String
    fun isLoggedIn(): Boolean
    fun isNewUser(): Boolean
    fun getToken(): String
    fun isWhatsAppConsentEnabled(): Boolean
    fun setIsWhatsAppConsentEnabled(flag: Boolean)
    fun setLoggedInTimeStamp(value: Long)
    fun getLoggedInTimeStamp(): Long
}
