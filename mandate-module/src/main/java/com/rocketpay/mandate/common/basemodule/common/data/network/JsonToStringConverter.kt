package com.rocketpay.mandate.common.basemodule.common.data.network

import com.google.gson.*
import java.lang.reflect.Type

internal class JsonToStringConverter : JsonSerializer<String?>, JsonDeserializer<String?> {
    override fun serialize(src: String?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) {
            return JsonNull.INSTANCE
        }
        val parser = JsonParser()
        return parser.parse(src).asJsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): String? {
        return json.toString()
    }
}
