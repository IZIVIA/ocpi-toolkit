package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.Instant

class OcpiInstantDeserializer : StdDeserializer<Instant>(Instant::class.java) {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant {
        return Instant.parse(
            parser.valueAsString.run {
                if (!endsWith("Z")) {
                    "${this}Z"
                } else {
                    this
                }
            },
        )
    }
}
