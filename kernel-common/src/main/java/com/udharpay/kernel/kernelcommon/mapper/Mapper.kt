package com.udharpay.kernel.kernelcommon.mapper

interface Mapper<S, D> {
    fun map(source: S): D
}
