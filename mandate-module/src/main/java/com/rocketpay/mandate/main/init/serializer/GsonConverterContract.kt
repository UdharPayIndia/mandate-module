package com.rocketpay.mandate.main.init.serializer

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rocketpay.mandate.common.jsonconverter.JsonConverterContract
import com.udharpay.kernel.kernelcommon.register.Register
import retrofit2.Converter
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

internal class GsonConverterContract: JsonConverterContract<Gson> {

    override fun getInstance(typeAdapters: Register<Type, Any>): Gson {
        val gsonBuilder = GsonBuilder()
        typeAdapters.getEntries().forEach {
            gsonBuilder.registerTypeAdapter(it.key, it.value)
        }
        return gsonBuilder.create()
    }

    override fun getRetrofitFactory(converter: Gson): Converter.Factory {
        return GsonConverterFactory.create(converter)
    }

    override fun <T> fromJson(converter: Gson, sourceString: String?, classOfDestination: Class<T>): T? {
        return converter.fromJson(sourceString, classOfDestination)
    }

    override fun <T> fromJson(converter: Gson, sourceString: String?, typeOfDestination: Type): T? {
        return converter.fromJson(sourceString, typeOfDestination)
    }

    override fun toJson(converter: Gson, sourceObject: Any?): String? {
        return converter.toJson(sourceObject)
    }

    override fun toJson(converter: Gson, sourceObject: Any?, typeOfSource: Type): String? {
        return converter.toJson(sourceObject, typeOfSource)
    }
}
