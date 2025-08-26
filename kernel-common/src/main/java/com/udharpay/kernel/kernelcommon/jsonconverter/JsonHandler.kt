package com.udharpay.kernel.kernelcommon.jsonconverter

import java.lang.reflect.Type

interface JsonHandler {

    fun <T> fromJson(sourceString: String?, classOfDestination: Class<T>): T?

    fun <T> fromJson(sourceString: String?, typeOfDestination: Type): T?

    fun toJson(sourceObject: Any?): String?

    fun toJson(sourceObject: Any?, typeOfSource: Type): String?
}
