package com.udharpay.core.networkmanager.data

import com.udharpay.core.networkmanager.client.NetworkManager
import com.udharpay.core.networkmanager.domain.entities.HttpMethod
import okhttp3.MultipartBody

class NetworkRequestBuilder {
    private var url: String = ""
    private var baseUrl: String = ""
    private var subUrl: String = ""
    private var headers = hashMapOf<String, String>()
    private var queryParameters = hashMapOf<String, String>()
    private var pathParameters = hashMapOf<String, String>()

    private var httpMethod: HttpMethod? = null
    private var requestBody: Any? = null

    private var multipartBodies = mutableListOf<MultipartBody.Part>()

    constructor()

    constructor(networkRequest: NetworkRequest) {
        this.httpMethod = networkRequest.httpMethod
        this.url = networkRequest.url
        this.headers = networkRequest.headers
        this.queryParameters = networkRequest.queryParameters
        this.requestBody = networkRequest.requestBody
        this.multipartBodies = networkRequest.multipartBodies
    }

    fun httpMethod(httpMethod: HttpMethod): NetworkRequestBuilder {
        this.httpMethod = httpMethod
        return this
    }

    fun url(url: String): NetworkRequestBuilder {
        if (url.isEmpty()) {
            throw IllegalArgumentException("Url cannot be empty")
        }

        this.url = url
        return this
    }

    fun baseUrl(baseUrl: String): NetworkRequestBuilder {
        if (baseUrl.isEmpty()) {
            throw IllegalArgumentException("baseUrl cannot be empty")
        }

        this.baseUrl = baseUrl
        return this
    }

    fun subUrl(subUrl: String): NetworkRequestBuilder {
        if (subUrl.isEmpty()) {
            throw IllegalArgumentException("subUrl cannot be empty")
        }

        this.subUrl = subUrl
        return this
    }

    fun pathParameter(key: String, value: String): NetworkRequestBuilder {
        addMapValue(this.pathParameters, key, value)
        return this
    }

    fun pathParameters(pathParameters: java.util.HashMap<String, String>): NetworkRequestBuilder {
        addEntireMap(pathParameters, this.pathParameters)
        return this
    }

    fun header(key: String, value: String): NetworkRequestBuilder {
        addMapValue(this.headers, key, value)
        return this
    }

    fun headers(headers: java.util.HashMap<String, String>): NetworkRequestBuilder {
        addEntireMap(headers, this.headers)
        return this
    }

    fun queryParameter(key: String, value: String): NetworkRequestBuilder {
        addMapValue(this.queryParameters, key, value)
        return this
    }

    fun queryParameters(queryParameters: java.util.HashMap<String, String>): NetworkRequestBuilder {
        addEntireMap(queryParameters, this.queryParameters)
        return this
    }

    fun body(requestBody: Any): NetworkRequestBuilder {
        this.requestBody = requestBody
        this.httpMethod = HttpMethod.Post
        return this
    }

    fun multipartBody(multipartBody: MultipartBody.Part): NetworkRequestBuilder {
        this.multipartBodies.add(multipartBody)
        return this
    }

    fun multipartBodies(multipartBodies: List<MultipartBody.Part>): NetworkRequestBuilder {
        this.multipartBodies.addAll(multipartBodies)
        return this
    }

    fun build(): NetworkRequest {
        return NetworkRequest(
            httpMethod,
            getUrl(),
            headers,
            queryParameters,
            requestBody,
            multipartBodies
        )
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Helper Methods //////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private fun addMapValue(map: MutableMap<String, String>, key: String, value: String) {
        if (key.isEmpty()) {
            throw IllegalArgumentException("Param key: $key is empty, url: ${this.url}")
        }
        map[key] = value
    }

    private fun addEntireMap(
        sourceMap: MutableMap<String, String>,
        destinationMap: MutableMap<String, String>
    ) {
        if (sourceMap.isEmpty()) throw IllegalArgumentException("Param cannot be empty")

        sourceMap.entries.forEach { entry ->
            apply {
                addMapValue(destinationMap, entry.key, entry.value)
            }
        }
    }

    private fun getUrl(): String {
        val url = when {
            url.isNotEmpty() -> {
                this.url
            }
            baseUrl.isNotEmpty() -> {
                "$baseUrl/$subUrl"
            }
            else -> {
                val baseUrl = NetworkManager.getInstance().getBaseUrl()
                if (baseUrl.isNotEmpty()) {
                    "$baseUrl/$subUrl"
                } else {
                    throw IllegalArgumentException("url cannot be empty")
                }
            }
        }

        return replacePathParametersWithValues(url, pathParameters)
    }

    private fun replacePathParametersWithValues(
        url: String,
        pathParams: Map<String, String>
    ): String {
        var transformedUrl: String = url

        for ((key, value) in pathParams) {
            transformedUrl = transformedUrl.replace("{$key}", value)
        }

        return transformedUrl
    }
}
