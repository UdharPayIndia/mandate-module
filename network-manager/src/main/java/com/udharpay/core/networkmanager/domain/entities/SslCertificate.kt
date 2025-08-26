package com.udharpay.core.networkmanager.domain.entities

data class SslCertificate(val domain: String, val publicKeys: List<String>)
