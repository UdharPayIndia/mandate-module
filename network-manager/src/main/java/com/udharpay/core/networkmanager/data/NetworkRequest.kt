package com.udharpay.core.networkmanager.data

import com.google.gson.reflect.TypeToken
import com.udharpay.core.networkmanager.client.NetworkManager
import com.udharpay.core.networkmanager.domain.entities.HttpMethod
import com.udharpay.core.networkmanager.domain.entities.Outcome
import okhttp3.MultipartBody

data class NetworkRequest(
    var httpMethod: HttpMethod? = null,
    val url: String,
    val headers: HashMap<String, String>,
    val queryParameters: HashMap<String, String>,
    val requestBody: Any? = null,
    val multipartBodies: MutableList<MultipartBody.Part>
)

suspend inline fun <reified T> NetworkRequest.get() : Outcome<T> {
    this.httpMethod = HttpMethod.Get
    return NetworkManager.getInstance().get(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.post() : Outcome<T> {
    this.httpMethod = HttpMethod.Post
    return NetworkManager.getInstance().post(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.patch() : Outcome<T> {
    this.httpMethod = HttpMethod.Patch
    return NetworkManager.getInstance().patch(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.put() : Outcome<T> {
    this.httpMethod = HttpMethod.Put
    return NetworkManager.getInstance().put(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.delete() : Outcome<T> {
    this.httpMethod = HttpMethod.Delete
    return NetworkManager.getInstance().delete(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.head() : Outcome<T> {
    this.httpMethod = HttpMethod.Head
    return NetworkManager.getInstance().head(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.getWithStream() : Outcome<T> {
    return NetworkManager.getInstance().getWithStream(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.postWithMultipart() : Outcome<T> {
    return NetworkManager.getInstance().postWithMultipart(this, object : TypeToken<T>(){}.type)
}

suspend inline fun <reified T> NetworkRequest.apiCall() : Outcome<T> {
    return NetworkManager.getInstance().apiCall(this, object : TypeToken<T>(){}.type)
}
