package com.udharpay.core.networkmanager.domain.usecase

import com.udharpay.core.networkmanager.data.NetworkRequest
import com.udharpay.core.networkmanager.domain.entities.FallbackGenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.GenericErrorResponse
import com.udharpay.core.networkmanager.domain.entities.NetworkError
import com.udharpay.core.networkmanager.domain.entities.Outcome
import com.udharpay.core.networkmanager.domain.interceptor.RequestInterceptor
import com.udharpay.core.networkmanager.domain.interceptor.ResponseInterceptor
import com.udharpay.kernel.kernelcommon.jsonconverter.JsonHandler
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.reflect.Type
import java.net.UnknownHostException

class ApiCallHandler(
    private val jsonHandler: JsonHandler,
    private val networkRequestInterceptors: MutableList<RequestInterceptor>,
    private val networkResponseInterceptors: MutableList<ResponseInterceptor>
) {

    suspend fun <T> handleApiCall(type: Type, networkRequest: NetworkRequest, block: suspend(NetworkRequest) -> Response<ResponseBody>): Outcome<T> {
        val response: Response<ResponseBody>
        try {
            response = block(notifyRequestInterceptor(networkRequest))
        } catch (e: UnknownHostException) {
            val genericErrorResponse = GenericErrorResponse(NetworkError.InternetConnectionFailed.code, NetworkError.InternetConnectionFailed.message, 0)
            return notifyResponseInterceptor(Outcome.Error(genericErrorResponse), networkRequest)
        }catch (e: Exception){
            val genericErrorResponse = GenericErrorResponse(NetworkError.ExecutionFailed.code, NetworkError.ExecutionFailed.message, 0)
            return notifyResponseInterceptor(Outcome.Error(genericErrorResponse), networkRequest)
        }

        var outcome: Outcome<T>
        try {
            outcome = if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                if (responseBody != null) {
                    Outcome.Success(jsonHandler.fromJson(responseBody, type)!!)
                } else {
                    val genericErrorResponse = GenericErrorResponse(NetworkError.MissingResponse.code, NetworkError.MissingResponse.message, response.code())
                    Outcome.Error(genericErrorResponse)
                }
            } else {
                val genericErrorResponse = parseError(response)
                Outcome.Error(genericErrorResponse)
            }
        } catch (e: Exception) {
            val genericErrorResponse = GenericErrorResponse(NetworkError.Unknown.code, NetworkError.Unknown.message, response.code())
            outcome = Outcome.Error(genericErrorResponse)
        }

        return notifyResponseInterceptor(outcome, networkRequest)
    }

    private fun <T> parseError(response: Response<T>): GenericErrorResponse {
        val errorResponseBody = response.errorBody()
        return if (errorResponseBody != null) {
            try {
                val errorString = errorResponseBody.string()
                val genericErrorResponse = jsonHandler.fromJson(errorString, GenericErrorResponse::class.java)!!
                genericErrorResponse.status = response.code()
                if (genericErrorResponse.code != null) {
                    genericErrorResponse
                } else {
                    val fallbackGenericErrorResponse = jsonHandler.fromJson(errorString, FallbackGenericErrorResponse::class.java)!!
                    GenericErrorResponse(fallbackGenericErrorResponse.code ?: NetworkError.ErrorCodeMissing.code, fallbackGenericErrorResponse.message ?: "", response.code())
                }
            } catch (e: Exception) {
                GenericErrorResponse(NetworkError.ParsingFailed.code, NetworkError.ParsingFailed.message, response.code())
            }
        } else {
            GenericErrorResponse(NetworkError.MissingErrorResponse.code, NetworkError.MissingErrorResponse.message, response.code())
        }
    }

    private suspend fun notifyRequestInterceptor(networkRequest: NetworkRequest): NetworkRequest {
        var tempNetworkRequest = networkRequest
        networkRequestInterceptors.forEach {
            tempNetworkRequest = it.intercept(tempNetworkRequest)
        }
        return tempNetworkRequest
    }

    private suspend fun <T> notifyResponseInterceptor(outcome: Outcome<T>, networkRequest: NetworkRequest): Outcome<T> {
        var tempOutcome = outcome
        networkResponseInterceptors.forEach {
            tempOutcome = it.intercept(tempOutcome, networkRequest)
        }
        return tempOutcome
    }
}
