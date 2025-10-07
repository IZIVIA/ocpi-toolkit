package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("java.time.Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        val formattedInstant = value
            .truncatedTo(ChronoUnit.MILLIS)
            .atOffset(ZoneOffset.UTC)
            .format(DateTimeFormatter.ISO_INSTANT)
        encoder.encodeString(formattedInstant)
    }

    override fun deserialize(decoder: Decoder): Instant {
        val dateString = decoder.decodeString()
        return try {
            Instant.parse(dateString)
        } catch (e: DateTimeParseException) {
            // might be missing the timezone information, we'll just try again using LocalDateTime assuming UTC
            LocalDateTime.parse(dateString).toInstant(ZoneOffset.UTC)
        }
    }
}
