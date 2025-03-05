package com.izivia.ocpi.toolkit.mappers

import com.fasterxml.jackson.module.kotlin.readValue
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.domain.ParkingType
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

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
        val instantWithZ = "2025-03-05T10:37:42.42Z"
        val instantWithoutZ = "2025-03-05T10:37:42.42"

        expectThat(
            mapper.readValue<InstantWrapper>("""{"instant": "$instantWithZ"}"""),
        ).isEqualTo(
            InstantWrapper(Instant.parse(instantWithZ)),
        )

        expectThat(
            mapper.readValue<InstantWrapper>("""{"instant": "$instantWithoutZ"}"""),
        ).isEqualTo(
            InstantWrapper(Instant.parse(instantWithZ)),
        )
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
