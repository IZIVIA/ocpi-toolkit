package com.izivia.ocpi.toolkit.tests.integration

import com.izivia.ocpi.toolkit.common.Header
import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.common.context.RequestMessageRoutingHeaders
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspClient
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsCpoService
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.*
import com.izivia.ocpi.toolkit.tests.integration.common.BaseServerIntegrationTest
import com.izivia.ocpi.toolkit.tests.integration.mock.LocationsCpoMongoRepository
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.mongodb.client.MongoDatabase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.litote.kmongo.getCollection
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant
import java.util.*
import kotlin.math.min

class LocationsIntegrationTest : BaseServerIntegrationTest() {

    private val tokenC = UUID.randomUUID().toString()
    private var database: MongoDatabase? = null

    private fun setupCpoServer(locations: List<Location>): Http4kTransportServer {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val collection = database!!.getCollection<Location>("cpo-server-locations-${UUID.randomUUID()}")
        collection.insertMany(locations)
        val server = buildTransportServer(DummyPartnerCacheRepository())
        runBlocking {
            LocationsCpoServer(
                LocationsCpoService(
                    service = LocationsCpoMongoRepository(collection),
                ),
                versionsRepository = InMemoryVersionsRepository(),
            ).registerOn(server)
        }
        return server
    }

    @Test
    fun `getLocations test (paginated)`() {
        // Start CPO server with dummy data
        val numberOfLocations = 500
        val referenceDate = Instant.parse("2022-04-28T09:00:00.000Z")
        val lastDate = referenceDate.plusSeconds(3600L * (numberOfLocations - 1))

        val cpoServer = setupCpoServer(
            locations = (0 until numberOfLocations).map { index ->
                validLocation.copy(
                    evses = listOf(validEvse.copy(connectors = listOf(validConnector))),
                    lastUpdated = referenceDate.plusSeconds(3600L * index),
                )
            },
        )
        cpoServer.start()

        val cpoServerVersionsUrl = "${cpoServer.baseUrl}/versions"

        val partnerRepo = DummyPartnerCacheRepository().also {
            val versionDetailsCpo = VersionsCacheRepository(baseUrl = cpoServer.baseUrl)

            runBlocking {
                it.saveEndpoints(
                    cpoServerVersionsUrl,
                    versionDetailsCpo.getVersionDetails(VersionNumber.V2_2_1)!!.endpoints,
                )
            }
        }

        val locationsEmspClient = LocationsEmspClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            partnerId = cpoServerVersionsUrl,
            partnerRepository = partnerRepo,
        )

        // Tests
        var limit = numberOfLocations + 1
        var offset = 0
        var dateFrom: Instant? = null
        var dateTo: Instant? = null

        val requestMessageRoutingHeaders = RequestMessageRoutingHeaders(
            toPartyId = "AAA",
            toCountryCode = "AA",
            fromPartyId = "BBB",
            fromCountryCode = "BB",
        )

        expectThat(
            runBlocking(requestMessageRoutingHeaders) {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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

        expectThat(cpoServer.requestHistory)
            .hasSize(1)[0]
            .and {
                get { first }.and {
                    // request
                    get { method }.isEqualTo(HttpMethod.GET)
                    get { path }.isEqualTo("/2.2.1/locations")
                    get { headers[Header.OCPI_FROM_PARTY_ID] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.fromPartyId)
                    get { headers[Header.OCPI_FROM_COUNTRY_CODE] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.fromCountryCode)
                    get { headers[Header.OCPI_TO_PARTY_ID] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.toPartyId)
                    get { headers[Header.OCPI_TO_COUNTRY_CODE] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.toCountryCode)
                }

                get { second }.and {
                    // response
                    get { headers[Header.OCPI_FROM_PARTY_ID] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.toPartyId)
                    get { headers[Header.OCPI_FROM_COUNTRY_CODE] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.toCountryCode)
                    get { headers[Header.OCPI_TO_PARTY_ID] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.fromPartyId)
                    get { headers[Header.OCPI_TO_COUNTRY_CODE] }.isNotNull()
                        .isEqualTo(requestMessageRoutingHeaders.fromCountryCode)
                }
            }

        limit = 100
        offset = 100
        dateFrom = null
        dateTo = null

        expectThat(
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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
                        .isEqualTo("${cpoServer.baseUrl}/2.2.1/locations?limit=$limit&offset=${offset + limit}")
                }
        }

        limit = 50
        offset = 50
        dateFrom = null
        dateTo = null

        expectThat(
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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
                        .isEqualTo("${cpoServer.baseUrl}/2.2.1/locations?limit=$limit&offset=${offset + limit}")
                }
        }

        limit = numberOfLocations + 1
        offset = 0
        dateFrom = referenceDate
        dateTo = lastDate

        expectThat(
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfLocations))

                    get { list }
                        .first()
                        .isA<Location>()
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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Location>()
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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Location>()
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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

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
            runBlocking {
                locationsEmspClient.getLocations(
                    dateFrom = dateFrom,
                    dateTo = dateTo,
                    offset = offset,
                    limit = limit,
                )
            },
        ) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Location>()
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
                    get { nextPageUrl }.isEqualTo(
                        "${cpoServer.baseUrl}/2.2.1/locations?date_to=$dateTo&limit=$limit&offset=${offset + limit}",
                    )
                }
        }
    }
}
