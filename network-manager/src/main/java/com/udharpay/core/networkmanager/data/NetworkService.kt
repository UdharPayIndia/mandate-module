package com.udharpay.core.networkmanager.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface NetworkService {

    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>
    ): Response<ResponseBody>

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
        @Body body: Any?
    ): Response<ResponseBody>

    @PATCH
    suspend fun patch(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
        @Body body: Any?
    ): Response<ResponseBody>

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
        @Body body: Any?
    ): Response<ResponseBody>

    @HTTP(method = "DELETE", hasBody = true)
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
        @Body body: Any?
    ): Response<ResponseBody>

    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
    ): Response<ResponseBody>

    @HEAD
    suspend fun head(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>
    ): Response<ResponseBody>

    @GET
    @Streaming
    suspend fun getWithStream(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>
    ): Response<ResponseBody>

    @POST
    @Multipart
    suspend fun postWithMultipart(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @QueryMap queries: Map<String, String>,
        @Part partList: List<MultipartBody.Part>
    ): Response<ResponseBody>
}
