package com.udharpay.core.networkmanager.domain.entities

sealed class HttpMethod(val value: String) {
    object Get : HttpMethod("GET")
    object Post : HttpMethod("POST")
    object Put : HttpMethod("PUT")
    object Delete : HttpMethod("DELETE")
    object Head : HttpMethod("HEAD")
    object Patch : HttpMethod("PATCH")

    companion object {
        val map by lazy {
            mapOf(
                "GET" to Get,
                "POST" to Post,
                "PUT" to Put,
                "DELETE" to Delete,
                "HEAD" to Head,
                "PATCH" to Patch
            )
        }
    }
}
