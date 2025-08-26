package com.rocketpay.mandate.feature.login.data

import com.rocketpay.mandate.feature.login.data.datasource.local.LoginDataStore
import com.rocketpay.mandate.feature.login.data.datasource.remote.LoginService
import com.rocketpay.mandate.feature.login.data.entities.CreateDeviceRequest
import com.rocketpay.mandate.feature.login.data.entities.DeviceResponseDto
import com.rocketpay.mandate.feature.login.data.entities.OtpRequest
import com.rocketpay.mandate.feature.login.data.entities.Signature
import com.rocketpay.mandate.feature.login.data.entities.ValidateOtpRequest
import com.rocketpay.mandate.feature.login.data.mapper.UserMapper
import com.rocketpay.mandate.feature.login.domain.entities.User
import com.rocketpay.mandate.feature.login.domain.repositories.LoginRepository
import com.rocketpay.mandate.main.GlobalState
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class LoginRepositoryImpl(
    private val loginDataStore: LoginDataStore,
    private val loginService: LoginService,
    private val userMapper: UserMapper
): LoginRepository {

    override fun getMobileNumber(): String {
        return loginDataStore.getMobileNumber()
    }

    override suspend fun requestOtp(mobileNumber: String, appSignature: String): Outcome<Boolean> {
        val otpRequest = OtpRequest(subjectId = mobileNumber, context = Signature(appSignature))
        return when(val outcome = loginService.requestOtp(otpRequest)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> Outcome.Success(true)
        }
    }

    override suspend fun verifyOtp(
        mobileNumber: String,
        enterpriseId: String,
        otp: String,
        deviceId: String
    ): Outcome<User> {
        val request = ValidateOtpRequest(subjectId = mobileNumber, enterpriseId = enterpriseId, secret = otp, deviceId = deviceId)
        return when(val outcome = loginService.verifyOtp(request)) {
            is Outcome.Error -> outcome
            is Outcome.Success -> {
                val user = userMapper.map(outcome.data)
                loginDataStore.setToken(user.token)
                loginDataStore.setMobileNumber(user.mobileNumber)
                loginDataStore.setNewUser(user.newUser)
                loginDataStore.setUserId(user.userId)
                loginDataStore.setAccountId(user.accountId)
                loginDataStore.setLoggedInTimeStamp(System.currentTimeMillis())
                Outcome.Success(user)
            }
        }
    }

    override suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Outcome<DeviceResponseDto>{
        return loginService.createDevice(createDeviceRequest)
    }

    override fun getUserId(): String {
        return loginDataStore.getUserId()
    }

    override fun getAccountId(): String {
        return loginDataStore.getAccountId()
    }

    override fun getCountryCode(): String {
        return loginDataStore.getCountryCode()
    }

    override fun setName(name: String) {
        return loginDataStore.setName(name)
    }

    override fun getName(): String {
        return loginDataStore.getName()
    }

    override fun isLoggedIn(): Boolean {
        return loginDataStore.isLoggedIn()
    }

    override fun isNewUser(): Boolean {
        return loginDataStore.isNewUser()
    }

    override fun getToken(): String{
        return loginDataStore.getToken()
    }

    override fun isWhatsAppConsentEnabled(): Boolean {
        return loginDataStore.isWhatsAppConsentEnabled()
    }

    override fun setIsWhatsAppConsentEnabled(flag: Boolean) {
        loginDataStore.setIsWhatsAppConsentEnabled(flag)
    }

    override fun setLoggedInTimeStamp(value: Long) {
        loginDataStore.setLoggedInTimeStamp(value)
    }

    override fun getLoggedInTimeStamp(): Long {
        return loginDataStore.getLoggedInTimeStamp()
    }
}
