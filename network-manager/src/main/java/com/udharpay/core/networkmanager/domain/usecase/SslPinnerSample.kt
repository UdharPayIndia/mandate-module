package com.udharpay.core.networkmanager.domain.usecase

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit

class SslPinnerSample(val converter: Converter.Factory) {

    var httpBuilder = OkHttpClient.Builder()

    var certificatePinner = CertificatePinner.Builder()
        .add("api.github.com", "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=")
        .build()

    var client = httpBuilder.certificatePinner(certificatePinner).build()

    var retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("https://api.github.com/")
        .addConverterFactory(converter)
        .build()
}
