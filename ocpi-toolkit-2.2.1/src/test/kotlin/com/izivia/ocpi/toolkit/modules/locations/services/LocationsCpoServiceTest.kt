package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.OcpiClientInvalidParametersException
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit.modules.locations.mock.locationsCpoRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectDoesNotThrow
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsCpoServiceTest {
    private lateinit var service: LocationsCpoInterface
    private val from = Instant.parse("2022-04-28T08:00:00.000Z")
    private val to = Instant.parse("2022-04-28T09:00:00.000Z")

    @Test
    fun getLocationsParamsValidationTest() {
        service = LocationsCpoValidator(service = locationsCpoRepository(emptyList()))

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = from, offset = 0, limit = null) },
        ) {
            get { offset }.isEqualTo(0)
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getLocations(dateFrom = to, dateTo = from, offset = 0, limit = null) }
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = to, offset = 0, limit = null) },
        ) {
            get { offset }.isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = to, offset = 0, limit = null) },
        ) {
            get { offset }.isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = null, offset = 0, limit = null) },
        ) {
            get { offset }.isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = null) },
        ) {
            get { offset }.isEqualTo(0)
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = -10, limit = null) }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = -10) }
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 100) },
        ) {
            get { offset }.isEqualTo(0)
            get { limit }.isEqualTo(100)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 100, limit = 100) },
        ) {
            get { offset }.isEqualTo(100)
            get { limit }.isEqualTo(100)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 0) },
        ) {
            get { offset }.isEqualTo(0)
            get { limit }.isEqualTo(0)
        }
    }

    @Test
    fun getLocationParamsValidationTest() {
        service = LocationsCpoValidator(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectDoesNotThrow {
            runBlocking { service.getLocation(locationId = str3chars) }
        }

        expectDoesNotThrow {
            runBlocking { service.getLocation(locationId = str36chars) }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getLocation(locationId = str37chars) }
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsCpoValidator(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectDoesNotThrow {
            runBlocking { service.getEvse(locationId = str3chars, evseUid = str3chars) }
        }

        expectDoesNotThrow {
            runBlocking { service.getEvse(locationId = str36chars, evseUid = str3chars) }
        }

        expectDoesNotThrow {
            runBlocking { service.getEvse(locationId = str3chars, evseUid = str36chars) }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getEvse(locationId = str37chars, evseUid = str3chars) }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getEvse(locationId = str3chars, evseUid = str37chars) }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking { service.getEvse(locationId = str37chars, evseUid = str37chars) }
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service = LocationsCpoValidator(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    locationId = str36chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str36chars,
                    connectorId = str3chars,
                )
            }
        }

        expectDoesNotThrow {
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str36chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    locationId = str37chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str37chars,
                    connectorId = str3chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str37chars,
                )
            }
        }

        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.getConnector(
                    locationId = str37chars,
                    evseUid = str37chars,
                    connectorId = str37chars,
                )
            }
        }
    }
}
