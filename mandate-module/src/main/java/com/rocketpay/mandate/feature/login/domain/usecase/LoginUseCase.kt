package com.rocketpay.mandate.feature.login.domain.usecase

import com.rocketpay.mandate.feature.login.data.entities.CreateDeviceRequest
import com.rocketpay.mandate.feature.login.data.entities.DeviceResponseDto
import com.rocketpay.mandate.feature.login.domain.entities.User
import com.rocketpay.mandate.feature.login.domain.repositories.AppSignatureRepository
import com.rocketpay.mandate.feature.login.domain.repositories.LoginRepository
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.rocketpay.mandate.common.basemodule.common.presentation.utils.DataValidator

internal class LoginUseCase internal constructor(
    private val loginRepository: LoginRepository,
    private val dataValidator: DataValidator,
    private val appSignatureRepository: AppSignatureRepository
) {
    fun validateCountryCode(countryCode: String): Boolean {
        return dataValidator.isValidCountryCode(countryCode)
    }

    fun validateMobileNumber(mobileNumber: String): Boolean {
        return dataValidator.isValidMobileNumber(mobileNumber)
    }

    fun validateOtp(otp: String): Boolean {
        return dataValidator.isValidOtp(otp)
    }

    suspend fun requestOtp(mobileNumber: String): Outcome<Boolean> {
        val appSignature = appSignatureRepository.getAppSignature()
        return loginRepository.requestOtp(mobileNumber, appSignature)
    }


    suspend fun verifyOtp(mobileNumber: String,
                          enterpriseId: String,
                          otp: String,
                          deviceId: String): Outcome<User> {
        return loginRepository.verifyOtp(mobileNumber = mobileNumber, enterpriseId = enterpriseId, otp = otp, deviceId = deviceId)
    }

    internal suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Outcome<DeviceResponseDto>{
        return loginRepository.createDevice(createDeviceRequest)
    }

    suspend fun requestOtp(mobileNumber: String, appSignature: String): Outcome<Boolean> {
        return loginRepository.requestOtp(mobileNumber, appSignature)
    }

    fun getMobileNumber(): String {
        return loginRepository.getMobileNumber()
    }

    fun getCountryCode(): String {
        return loginRepository.getCountryCode()
    }

    fun setName(name: String) {
        loginRepository.setName(name)
    }

    fun getName(): String {
        return loginRepository.getName()
    }

    fun getUserId(): String {
        return loginRepository.getUserId()
    }

    fun getAccountId(): String {
        return loginRepository.getAccountId()
    }

    fun isLoggedIn(): Boolean {
        return loginRepository.isLoggedIn()
    }

    fun isNewUser(): Boolean {
        return loginRepository.isNewUser()
    }

    fun isWhatsAppConsentEnabled(): Boolean {
        return loginRepository.isWhatsAppConsentEnabled()
    }

    fun setIsWhatsAppConsentEnabled(flag: Boolean) {
        loginRepository.setIsWhatsAppConsentEnabled(flag)
    }

    fun setLoggedInTimeStamp(value: Long) {
        loginRepository.setLoggedInTimeStamp(value)
    }

    fun getLoggedInTimeStamp(): Long {
        return loginRepository.getLoggedInTimeStamp()
    }
}
