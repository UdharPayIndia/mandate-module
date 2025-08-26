package com.udharpay.core.networkmanager.domain.entities

sealed class Outcome<out T> {
    data class Success<out T>(val data: T) : Outcome<T>()
    data class Error(val error: GenericErrorResponse) : Outcome<Nothing>()
}
