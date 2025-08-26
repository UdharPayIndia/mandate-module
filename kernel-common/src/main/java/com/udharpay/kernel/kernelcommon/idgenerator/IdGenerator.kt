package com.udharpay.kernel.kernelcommon.idgenerator

import java.util.*

class IdGenerator {

    fun uuid(): String {
        val x = UUID.randomUUID()
        return internalUUID(x, currentTimeMillis())
    }

    private fun internalUUID(source: UUID, millis: Long): String {
        val low = source.leastSignificantBits
        return UUID(millis, low).toString()
    }

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
