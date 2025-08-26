package com.udharpay.core.networkmanager.domain.entities

sealed class NetworkError(val code: String, val message: String) {
    object InternetConnectionFailed: NetworkError("INTERNET_CONNECTION_FAILED", "No internet connectivity, please connect to internet.")
    object ExecutionFailed: NetworkError("EXECUTION_FAILED", "Api call execution failed")
    object MissingResponse: NetworkError("MISSING_RESPONSE", "Success response should not be null")
    object MissingErrorResponse: NetworkError("MISSING_ERROR_RESPONSE", "Error response should not be null")
    object ParsingFailed: NetworkError("PARSING_FAILED", "Unable to Parse Error Response")
    object Unknown: NetworkError("UNKNOWN_ERROR", "Something went wrong")
    object ErrorCodeMissing: NetworkError("ERROR_CODE_MISSING", "Unable get error code from error response")
}
