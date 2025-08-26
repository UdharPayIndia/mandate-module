package com.rocketpay.mandate.main.init.serializer

import com.udharpay.kernel.kernelcommon.jsonconverter.JsonHandler
import com.rocketpay.mandate.common.jsonconverter.JsonConverter
import java.lang.reflect.Type

internal class JsonHandlerImpl: JsonHandler {
    override fun <T> fromJson(sourceString: String?, classOfDestination: Class<T>): T? {
        return JsonConverter.getInstance().fromJson(sourceString, classOfDestination)
    }

    override fun <T> fromJson(sourceString: String?, typeOfDestination: Type): T? {
        return JsonConverter.getInstance().fromJson(sourceString, typeOfDestination)
    }

    override fun toJson(sourceObject: Any?): String? {
        return JsonConverter.getInstance().toJson(sourceObject)
    }

    override fun toJson(sourceObject: Any?, typeOfSource: Type): String? {
        return JsonConverter.getInstance().toJson(sourceObject, typeOfSource)
    }
}
