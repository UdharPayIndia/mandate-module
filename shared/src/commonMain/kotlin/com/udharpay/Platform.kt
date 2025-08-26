package com.udharpay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform