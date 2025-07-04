package com.izivia.ocpi.toolkit.mappers

import com.fasterxml.jackson.module.kotlin.readValue
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.domain.ParkingType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.comparesEqualTo
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class MappersTest {
    data class InstantWrapper(
        val instant: Instant,
    )

    @Test
    fun `should truncate instants when exceeds 25 characters`() {
        val location = buildLocation(Instant.parse("2018-01-01T01:08:01.123456789Z")) // contains 30 characters

        expectThat(mapper.writeValueAsString(location))
            .isJsonEqualTo(jsonLocation("2018-01-01T01:08:01.123Z"))
    }

    @Test
    fun `should not truncate instants when it does not exceed 25 characters`() {
        val location = buildLocation(Instant.parse("2018-01-01T01:08:01.123Z")) // contains 24 characters

        expectThat(
            mapper.writeValueAsString(location),
        ).isJsonEqualTo(
            jsonLocation("2018-01-01T01:08:01.123Z"),
        )
    }

    @Test
    fun `should properly parse with and without Z`() {
        val want = ZonedDateTime.of(2025, 3, 5, 10, 37, 42, 130_000_000, ZoneOffset.UTC).toInstant()

        listOf(
            "2025-03-05T10:37:42.13Z",
            "2025-03-05T10:37:42.13",
            "2025-03-05T10:37:42.13+00:00",
            "2025-03-05T11:37:42.13+01:00",
            "2025-03-05T09:37:42.13-01:00",
        ).forEach {
            expectThat(
                mapper.readValue<InstantWrapper>("""{"instant": "$it"}"""),
            ).get { instant }.comparesEqualTo(want)
        }

        listOf(
            "2025-03-05T10:37:42Z",
            "2025-03-05T10:37:42",
            "2025-03-05T10:37:42+00:00",
            "2025-03-05T11:37:42+01:00",
            "2025-03-05T09:37:42-01:00",
        ).forEach {
            expectThat(
                mapper.readValue<InstantWrapper>("""{"instant": "$it"}"""),
            ).get { instant }.comparesEqualTo(want.truncatedTo(ChronoUnit.SECONDS))
        }
    }
}

private fun buildLocation(timestamp: Instant) = Location(
    countryCode = "",
    partyId = "",
    id = "",
    publish = false,
    publishAllowedTo = listOf(),
    name = null,
    address = "",
    city = "",
    postalCode = null,
    state = null,
    country = "",
    coordinates = GeoLocation(latitude = "", longitude = ""),
    relatedLocations = listOf(),
    parkingType = ParkingType.PARKING_LOT,
    evses = listOf(),
    directions = listOf(),
    operator = null,
    suboperator = null,
    owner = null,
    facilities = listOf(),
    timeZone = "",
    openingTimes = null,
    chargingWhenClosed = null,
    images = listOf(),
    energyMix = null,
    lastUpdated = timestamp,
)

private fun jsonLocation(instant: String) = """
                {
                  "country_code": "",
                  "party_id": "",
                  "id": "",
                  "publish": false,
                  "publish_allowed_to": [],
                  "address": "",
                  "city": "",
                  "country": "",
                  "coordinates": {
                    "latitude": "",
                    "longitude": ""
                  },
                  "related_locations": [],
                  "parking_type": "PARKING_LOT",
                  "evses": [],
                  "directions": [],
                  "facilities": [],
                  "time_zone": "",
                  "images": [],
                  "last_updated": "$instant"
                }
""".trimIndent()
