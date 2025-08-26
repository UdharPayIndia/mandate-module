package com.rocketpay.mandate.common.jsonconverter

import com.udharpay.kernel.kernelcommon.register.Register
import retrofit2.Converter
import java.lang.reflect.Type

internal class JsonConverter<F> private constructor(
    private val jsonConverterContract: JsonConverterContract<F>,
    private val jsonInstance: F
) {

    companion object {
        private var instance: JsonConverter<*>? = null
        fun getInstance(): JsonConverter<*> {
            return instance ?: throw Exception("JsonManager is not built, Please use Builder to build")
        }
    }

    class Builder<F> {
        private lateinit var typeTypeAdapters: Register<Type, Any>
        private lateinit var contract: JsonConverterContract<F>

        fun setTypeAdapters(typeTypeAdapters: Register<Type, Any>) = apply {
            this.typeTypeAdapters = typeTypeAdapters
        }

        fun setContract(contract: JsonConverterContract<F>)  = apply {
            this.contract = contract
        }

        fun build() {
            if (instance != null) {
                throw Exception("JsonManager is already built, you can not Re-built")
            }

            instance = JsonConverter(
                contract,
                contract.getInstance(typeTypeAdapters)
            )
        }
    }

    fun getFactory(): Converter.Factory {
        return jsonConverterContract.getRetrofitFactory(jsonInstance)
    }

    fun <T> fromJson(sourceString: String?, classOfDestination: Class<T>): T? {
        return jsonConverterContract.fromJson(jsonInstance, sourceString, classOfDestination)
    }

    fun <T> fromJson(sourceString: String?, typeOfDestination: Type): T? {
        return jsonConverterContract.fromJson(jsonInstance, sourceString, typeOfDestination)
    }

    fun toJson(sourceObject: Any?): String? {
        return jsonConverterContract.toJson(jsonInstance, sourceObject)
    }

    fun toJson(sourceObject: Any?, typeOfSource: Type): String? {
        return jsonConverterContract.toJson(jsonInstance, sourceObject, typeOfSource)
    }
}
