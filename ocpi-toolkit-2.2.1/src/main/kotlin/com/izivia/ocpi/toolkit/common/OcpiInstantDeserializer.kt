package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

class OcpiInstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant {
        return try {
            Instant.parse(parser.valueAsString)
        } catch (e: DateTimeParseException) {
            // might be missing the timezone information, we'll just try again using LocalDateTime assuming UTC
            LocalDateTime.parse(parser.valueAsString).toInstant(ZoneOffset.UTC)
        }
    }
}
