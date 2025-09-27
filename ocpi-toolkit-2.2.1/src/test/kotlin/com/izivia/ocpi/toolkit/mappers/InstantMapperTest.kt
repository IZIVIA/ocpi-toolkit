package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.comparesEqualTo
import strikt.assertions.isEqualTo
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class InstantMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should truncate instants when exceeds 25 characters`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(Instant.parse("2018-01-01T01:08:01.123456789Z")))
            .isEqualTo("\"2018-01-01T01:08:01.123Z\"")
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should not truncate instants when it does not exceed 25 characters`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(Instant.parse("2018-02-03T01:08:01.123Z")))
            .isEqualTo("\"2018-02-03T01:08:01.123Z\"")
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should properly parse with and without Z`(serializer: OcpiSerializer) {
        val want = ZonedDateTime.of(2025, 3, 5, 10, 37, 42, 130_000_000, ZoneOffset.UTC).toInstant()

        listOf(
            "\"2025-03-05T10:37:42.13Z\"",
            "\"2025-03-05T10:37:42.13\"",
            "\"2025-03-05T10:37:42.13+00:00\"",
            "\"2025-03-05T11:37:42.13+01:00\"",
            "\"2025-03-05T09:37:42.13-01:00\"",
        ).forEach {
            expectThat(
                serializer.deserializeObject<Instant>(it),
            ).comparesEqualTo(want)
        }

        listOf(
            "\"2025-03-05T10:37:42Z\"",
            "\"2025-03-05T10:37:42\"",
            "\"2025-03-05T10:37:42+00:00\"",
            "\"2025-03-05T11:37:42+01:00\"",
            "\"2025-03-05T09:37:42-01:00\"",
        ).forEach {
            expectThat(
                serializer.deserializeObject<Instant>(it),
            ).comparesEqualTo(want.truncatedTo(ChronoUnit.SECONDS))
        }
    }
}
