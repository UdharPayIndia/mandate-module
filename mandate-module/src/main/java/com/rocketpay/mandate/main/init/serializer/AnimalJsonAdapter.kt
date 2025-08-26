package com.rocketpay.mandate.main.init.serializer

import com.google.gson.*
import java.lang.reflect.Type

internal class AnimalJsonAdapter: JsonSerializer<Animal>, JsonDeserializer<Animal> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Animal {
        val type = json?.asJsonObject?.get("type")?.asString
        return when (AnimalType.map[type]) {
            AnimalType.Cow -> context!!.deserialize(json, Cow::class.java)
            AnimalType.Dog -> context!!.deserialize(json, Dog::class.java)
            null -> throw JsonParseException("Field type was null in AnimalJsonAdapter")
        }
    }

    override fun serialize(
        src: Animal?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return when(AnimalType.map[src?.type]) {
            AnimalType.Cow -> context!!.serialize(src, Cow::class.java)
            AnimalType.Dog -> context!!.serialize(src, Dog::class.java)
            null -> throw JsonParseException("Field type was null in AnimalJsonAdapter")
        }
    }
}


open class Animal(val type: String)

class Cow(type: String, val name: String, var height: String): Animal(type) {
    override fun toString(): String {
        return "type: $type, name: $name, height: $height"
    }
}

class Dog(type: String, val name: String, var width: String): Animal(type) {
    override fun toString(): String {
        return "type: $type, name: $name, width: $width"
    }
}

sealed class AnimalType(val value: String) {
    object Cow: AnimalType("cow")
    object Dog: AnimalType("dog")

    companion object {
        val map by lazy {
            mapOf(
                "cow" to Cow,
                "dog" to Dog
            )
        }
    }
}
