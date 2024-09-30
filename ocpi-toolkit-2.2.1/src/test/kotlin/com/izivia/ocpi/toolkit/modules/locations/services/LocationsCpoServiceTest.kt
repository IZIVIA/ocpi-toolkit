package com.izivia.ocpi.toolkit.modules.locations.services

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.mock.locationsCpoRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.time.Instant
import java.util.*

class LocationsCpoServiceTest {
    private lateinit var service: LocationsCpoService
    private val from = Instant.parse("2022-04-28T08:00:00.000Z")
    private val to = Instant.parse("2022-04-28T09:00:00.000Z")

    @Test
    fun getLocationsParamsValidationTest() {
        service = LocationsCpoService(service = locationsCpoRepository(emptyList()))

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = from, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = to, dateTo = from, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = to, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = to, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = from, dateTo = null, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = -10, limit = null) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = -10) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 100) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(100)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 100, limit = 100) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(100)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(100)
        }

        expectThat(
            runBlocking { service.getLocations(dateFrom = null, dateTo = null, offset = 0, limit = 0) },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::offset)
                .isEqualTo(0)

            get { data }
                .isNotNull()
                .get(SearchResult<Location>::limit)
                .isEqualTo(0)
        }
    }

    @Test
    fun getLocationParamsValidationTest() {
        service =
            LocationsCpoService(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectThat(runBlocking { service.getLocation(locationId = str3chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getLocation(locationId = str36chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getLocation(locationId = str37chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getEvseParamsValidationTest() {
        service = LocationsCpoService(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectThat(runBlocking { service.getEvse(locationId = str3chars, evseUid = str3chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str36chars, evseUid = str3chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str37chars, evseUid = str3chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str3chars, evseUid = str3chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str3chars, evseUid = str36chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str3chars, evseUid = str37chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(runBlocking { service.getEvse(locationId = str37chars, evseUid = str37chars) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }

    @Test
    fun getConnectorParamsValidationTest() {
        service =
            LocationsCpoService(service = locationsCpoRepository(emptyList()))

        val str3chars = "abc"
        val str36chars = "abababababababababababababababababab"
        val str37chars = "ababababababababababababababababababa"

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str36chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str37chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str36chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str37chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str37chars,
                    evseUid = str37chars,
                    connectorId = str3chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str3chars,
                    evseUid = str3chars,
                    connectorId = str37chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            runBlocking {
                service.getConnector(
                    locationId = str37chars,
                    evseUid = str37chars,
                    connectorId = str37chars,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
