package com.rocketpay.mandate.common.jsonconverter

import com.udharpay.kernel.kernelcommon.register.Register
import retrofit2.Converter
import java.lang.reflect.Type

internal interface JsonConverterContract<F> {

    fun getInstance(typeAdapters: Register<Type, Any>): F

    fun getRetrofitFactory(converter: F): Converter.Factory

    fun <T> fromJson(converter: F, sourceString: String?, classOfDestination: Class<T>): T?

    fun <T> fromJson(converter: F, sourceString: String?, typeOfDestination: Type): T?

    fun toJson(converter: F, sourceObject: Any?): String?

    fun toJson(converter: F, sourceObject: Any?, typeOfSource: Type): String?
}
