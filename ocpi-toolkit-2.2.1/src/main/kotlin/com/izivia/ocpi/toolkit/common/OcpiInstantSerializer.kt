package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class OcpiInstantSerializer : StdSerializer<Instant>(Instant::class.java) {
    override fun serialize(value: Instant?, gen: JsonGenerator, provider: SerializerProvider) {
        if (value != null) {
            val formatted = value.truncatedTo(ChronoUnit.MILLIS).atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            gen.writeString(formatted)
        } else {
            gen.writeNull()
        }
    }
}
