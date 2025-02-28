package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class OcpiInstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant {
        val value = parser.text.trim()
        val localDateTime = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        return localDateTime.atOffset(ZoneOffset.UTC).toInstant()
    }
}
