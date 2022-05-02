package tests

import common.OcpiStatusCode
import kotlinx.coroutines.GlobalScope
import ocpi.locations.LocationsCpoServer
import ocpi.locations.LocationsEmspClient
import ocpi.locations.domain.Location
import ocpi.locations.services.LocationsCpoService
import org.junit.jupiter.api.Test
import org.litote.kmongo.getCollection
import samples.Http4kTransportClient
import samples.Http4kTransportServer
import strikt.api.expectThat
import strikt.assertions.*
import tests.mock.LocationsCpoMongoRepository
import java.time.Instant
import kotlin.math.min

class LocationsIntegrationTest : BaseDBIntegrationTest() {

    @Test
    fun getLocationsTest() {
        // Db setup
        val database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val collection = database.getCollection<Location>()
        val locationsCpoRepository = LocationsCpoMongoRepository(collection)

        // Add dummy data
        val numberOfLocations = 1000
        val referenceDate = Instant.parse("2022-04-28T09:00:00.000Z")
        val lastDate = referenceDate.plusSeconds(3600L * (numberOfLocations - 1))
        val locations = mutableListOf<Location>()
        (0 until numberOfLocations).forEach { index ->
            locations.add(
                dummyLocation.copy(
                    evses = listOf(dummyEvse.copy(connectors = listOf(dummyConnector))),
                    last_updated = referenceDate.plusSeconds(3600L * index)
                )
            )
        }
        collection.insertMany(locations)

        // Start CPO server
        val cpoServerPort = 8080
        val cpoServerUrl = "http://localhost:$cpoServerPort"
        val transport = Http4kTransportServer(cpoServerUrl, cpoServerPort)
        LocationsCpoServer(transport, LocationsCpoService(locationsCpoRepository))

        GlobalScope.run {
            transport.start()
        }

        val locationsEmspClient = LocationsEmspClient(Http4kTransportClient(cpoServerUrl))

        // Tests
        var limit = numberOfLocations + 1
        var offset = 0
        var dateFrom: Instant? = null
        var dateTo: Instant? = null

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = 100
        offset = 100
        dateFrom = null
        dateTo = null

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("http://localhost:8080/ocpi/cpo/2.1.1/locations?limit=$limit&offset=${offset + limit}")
                }
        }

        limit = 50
        offset = 50
        dateFrom = null
        dateTo = null

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("http://localhost:8080/ocpi/cpo/2.1.1/locations?limit=$limit&offset=${offset + limit}")
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = referenceDate
        dateTo = lastDate

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = referenceDate
        dateTo = null

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = null
        dateTo = lastDate

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = lastDate
        dateTo = null

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(1)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = null
        dateTo = referenceDate

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(1)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfLocations + 1
        offset = 1
        dateFrom = null
        dateTo = referenceDate

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isEmpty()
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(1)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = 1
        offset = 0
        dateFrom = null
        dateTo = lastDate.minusSeconds(3600L)

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatusCode.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)
                }
                .and {
                    get { limit }
                        .isEqualTo(limit)
                }
                .and {
                    get { offset }
                        .isEqualTo(offset)
                }
                .and {
                    get { totalCount }
                        .isEqualTo(numberOfLocations - 1)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("http://localhost:8080/ocpi/cpo/2.1.1/locations?date_to=${dateTo}&limit=$limit&offset=${offset+limit}")
                }
        }

        transport.stop()
    }
}
