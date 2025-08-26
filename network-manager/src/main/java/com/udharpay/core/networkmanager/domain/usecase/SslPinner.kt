package com.udharpay.core.networkmanager.domain.usecase

import com.udharpay.core.networkmanager.domain.entities.SslCertificate
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

class SslPinner {

    fun pin(okHttpClientBuilder: OkHttpClient.Builder, sslCertificates: List<SslCertificate>) {
        val certificatePinner = CertificatePinner.Builder()
        sslCertificates.forEach { sslCertificate ->
            sslCertificate.publicKeys.forEach { publicKey ->
                certificatePinner.add(sslCertificate.domain, publicKey)
            }
        }

        okHttpClientBuilder.certificatePinner(certificatePinner.build())
    }

    fun pin(okHttpClient: OkHttpClient, sslCertificates: List<SslCertificate>): OkHttpClient {
        val certificatePinner = CertificatePinner.Builder()
        sslCertificates.forEach { sslCertificate ->
            sslCertificate.publicKeys.forEach { publicKey ->
                certificatePinner.add(sslCertificate.domain, publicKey)
            }
        }

        return okHttpClient.newBuilder()
            .certificatePinner(certificatePinner.build())
            .build()
    }
}
