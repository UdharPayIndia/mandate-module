package com.rocketpay.mandate.feature.login.data.datasource.remote

import com.rocketpay.mandate.feature.login.data.entities.CreateDeviceRequest
import com.rocketpay.mandate.feature.login.data.entities.DeviceResponseDto
import com.rocketpay.mandate.feature.login.data.entities.OtpRequest
import com.rocketpay.mandate.feature.login.data.entities.UserDto
import com.rocketpay.mandate.feature.login.data.entities.ValidateOtpRequest
import com.udharpay.core.networkmanager.data.NetworkRequestBuilder
import com.udharpay.core.networkmanager.data.post
import com.udharpay.core.networkmanager.domain.entities.GenericSuccessResponse
import com.udharpay.core.networkmanager.domain.entities.Outcome

internal class LoginService {

    suspend fun requestOtp(otpRequest: OtpRequest): Outcome<GenericSuccessResponse> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/auth/v2/request-secret")
            .body(otpRequest)
            .build()
            .post()
    }

    suspend fun verifyOtp(validateOtpRequest: ValidateOtpRequest): Outcome<UserDto> {
        return NetworkRequestBuilder()
            .subUrl("/common/party/v1/login")
            .body(validateOtpRequest)
            .build()
            .post()
    }

    suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Outcome<DeviceResponseDto> {
        return NetworkRequestBuilder()
            .subUrl("/api/merchant/devices")
            .body(createDeviceRequest)
            .build()
            .post()
    }

}
