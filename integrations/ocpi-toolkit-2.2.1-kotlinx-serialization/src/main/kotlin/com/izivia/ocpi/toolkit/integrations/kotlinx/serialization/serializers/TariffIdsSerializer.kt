package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull

class SkipNullsListSerializer<T>(
    private val elementSerializer: KSerializer<T>,
) : KSerializer<List<T>?> {

    private val listSerializer = ListSerializer(elementSerializer).nullable

    override val descriptor: SerialDescriptor = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<T>?) {
        listSerializer.serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): List<T>? {
        val jsonDecoder = decoder as? JsonDecoder
            ?: return listSerializer.deserialize(decoder)

        return when (val jsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> null
            is JsonArray -> {
                jsonElement
                    .filterNot { it is JsonNull }
                    .map { element ->
                        jsonDecoder.json.decodeFromJsonElement(elementSerializer, element)
                    }
            }

            else -> throw SerializationException("Expected array or null for List")
        }
    }
}
