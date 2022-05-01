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
        val referenceDate = Instant.parse("2022-04-28T09:00:00.000Z")
        val numberOfLocations = 1000
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

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = null,
                dateTo = null,
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

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = null,
                dateTo = null,
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

        expectThat(
            locationsEmspClient.getLocations(
                dateFrom = null,
                dateTo = null,
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

        transport.stop()
    }
}
