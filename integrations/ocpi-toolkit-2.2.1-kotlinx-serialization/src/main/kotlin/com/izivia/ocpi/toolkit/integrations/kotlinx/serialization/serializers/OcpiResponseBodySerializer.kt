package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer
import java.time.Instant

@OptIn(ExperimentalSerializationApi::class)
class OcpiResponseBodySerializer<T>(
    private val dataSerializer: KSerializer<T>,
) : KSerializer<OcpiResponseBody<T>> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OcpiResponseBody") {
        element("data", dataSerializer.descriptor, isOptional = true)
        element<Int>("statusCode")
        element<String?>("statusMessage", isOptional = true)
        element("timestamp", PrimitiveSerialDescriptor("java.time.Instant", PrimitiveKind.STRING))
    }

    override fun serialize(encoder: Encoder, value: OcpiResponseBody<T>) {
        encoder.encodeStructure(descriptor) {
            encodeNullableSerializableElement(descriptor, 0, dataSerializer, value.data)
            encodeIntElement(descriptor, 1, value.statusCode)
            encodeNullableSerializableElement(descriptor, 2, String.serializer(), value.statusMessage)
            encodeSerializableElement(descriptor, 3, encoder.serializersModule.serializer<Instant>(), value.timestamp)
        }
    }

    override fun deserialize(decoder: Decoder): OcpiResponseBody<T> {
        return decoder.decodeStructure(descriptor) {
            var data: T? = null
            var statusCode: Int? = null
            var statusMessage: String? = null
            var timestamp: Instant? = null

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> data = decodeNullableSerializableElement(descriptor, 0, dataSerializer)
                    1 -> statusCode = decodeIntElement(descriptor, 1)
                    2 -> statusMessage = decodeNullableSerializableElement(
                        descriptor, 2, String.serializer(),
                    )
                    3 -> timestamp = decodeSerializableElement(
                        descriptor, 3, decoder.serializersModule.serializer<Instant>(),
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            OcpiResponseBody(
                data = data,
                statusCode = statusCode ?: error("Missing statusCode"),
                statusMessage = statusMessage,
                timestamp = timestamp ?: error("Missing timestamp"),
            )
        }
    }
}
