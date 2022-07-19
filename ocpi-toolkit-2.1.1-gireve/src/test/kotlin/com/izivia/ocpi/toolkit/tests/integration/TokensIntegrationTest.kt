package com.izivia.ocpi.toolkit.tests.integration

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoClient
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.validation.TokensEmspValidationService
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.samples.common.*
import com.izivia.ocpi.toolkit.tests.integration.common.BaseServerIntegrationTest
import com.izivia.ocpi.toolkit.tests.integration.mock.TokensEmspMongoRepository
import com.mongodb.client.MongoDatabase
import org.junit.jupiter.api.Test
import org.litote.kmongo.getCollection
import strikt.api.expectThat
import strikt.assertions.*
import java.time.Instant
import java.util.*
import kotlin.math.min


class TokensIntegrationTest : BaseServerIntegrationTest() {

    private val tokenC = UUID.randomUUID().toString()
    private var database: MongoDatabase? = null

    private fun setupEmspServer(tokens: List<Token>): Http4kTransportServer {
        if (database == null) database = buildDBClient().getDatabase("ocpi-2-1-1-tests")
        val collection = database!!.getCollection<Token>("emsp-server-tokens-${UUID.randomUUID()}")
        collection.insertMany(tokens)
        val server = buildTransportServer()
        TokensEmspServer(
            server,
            platformRepository = DummyPlatformCacheRepository(tokenC = tokenC),
            TokensEmspValidationService(
                service = TokensEmspMongoRepository(collection),
            )
        )
        return server
    }

    @Test
    fun `getTokens test (paginated)`() {

        // Start EMSP server with dummy data
        val numberOfTokens = 500
        val referenceDate = Instant.parse("2022-04-28T09:00:00.000Z")
        val lastDate = referenceDate.plusSeconds(3600L * (numberOfTokens - 1))

        val emspServer = setupEmspServer(
            tokens = (0 until numberOfTokens).map { index ->
                validToken.copy(
                    uid = UUID.randomUUID().toString(),
                    last_updated = referenceDate.plusSeconds(3600L * index)
                )
            }
        )
        emspServer.start()

        val emspServerVersionsUrl = "${emspServer.baseUrl}/versions"

        val platformRepo = DummyPlatformCacheRepository(tokenC = tokenC).also {
            val versionDetailsEmsp = VersionDetailsCacheRepository(baseUrl = emspServer.baseUrl)

            it.saveEndpoints(
                emspServerVersionsUrl,
                versionDetailsEmsp.getVersionDetails(VersionNumber.V2_1_1)!!.endpoints
            )
        }

        val tokensCpoClient = TokensCpoClient(
            transportClientBuilder = Http4kTransportClientBuilder(),
            serverVersionsEndpointUrl = emspServerVersionsUrl,
            platformRepository = platformRepo
        )

        // Tests
        var limit = numberOfTokens + 1
        var offset = 0
        var dateFrom: Instant? = null
        var dateTo: Instant? = null
        val countryCode: String = "fr"
        val partyId: String = "abc"

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
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
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("${emspServer.baseUrl}/2.1.1/tokens?limit=$limit&ocpi-to-country-code=$countryCode&ocpi-to-party-id=$partyId&offset=${offset + limit}")
                }
        }

        limit = 50
        offset = 50
        dateFrom = null
        dateTo = null

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("${emspServer.baseUrl}/2.1.1/tokens?limit=$limit&ocpi-to-country-code=$countryCode&ocpi-to-party-id=$partyId&offset=${offset + limit}")
                }
        }

        limit = numberOfTokens + 1
        offset = 0
        dateFrom = referenceDate
        dateTo = lastDate

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfTokens + 1
        offset = 0
        dateFrom = referenceDate
        dateTo = null

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfTokens + 1
        offset = 0
        dateFrom = null
        dateTo = lastDate

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(min(limit, numberOfTokens))

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens)
                }
                .and {
                    get { nextPageUrl }
                        .isNull()
                }
        }

        limit = numberOfTokens + 1
        offset = 0
        dateFrom = lastDate
        dateTo = null

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Token>()
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

        limit = numberOfTokens + 1
        offset = 0
        dateFrom = null
        dateTo = referenceDate

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Token>()
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

        limit = numberOfTokens + 1
        offset = 1
        dateFrom = null
        dateTo = referenceDate

        expectThat(
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
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
            tokensCpoClient.getTokens(
                dateFrom = dateFrom,
                dateTo = dateTo,
                offset = offset,
                limit = limit,
                countryCode = countryCode,
                partyId = partyId
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .and {
                    get { list }
                        .isNotEmpty()
                        .hasSize(1)

                    get { list }
                        .first()
                        .isA<Token>()
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
                        .isEqualTo(numberOfTokens - 1)
                }
                .and {
                    get { nextPageUrl }
                        .isEqualTo("${emspServer.baseUrl}/2.1.1/tokens?date_to=${dateTo}&limit=$limit&ocpi-to-country-code=$countryCode&ocpi-to-party-id=$partyId&offset=${offset + limit}")
                }
        }
    }
}
