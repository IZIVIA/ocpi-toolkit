package com.izivia.ocpi.toolkit.modules.tokens.http.emsp

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.toSearchResult
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.EnergyContract
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensEmspRepository
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensEmspService
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class TokensEmspHttpGetTokensTest {
    @Test
    fun `should list tokens`() {
        val slots = object {
            var dateFrom = slot<Instant>()
            var dateTo = slot<Instant>()
        }
        val srv = mockk<TokensEmspRepository> {
            coEvery {
                getTokens(capture(slots.dateFrom), capture(slots.dateTo), any(), any())
            } coAnswers {
                listOf(
                    Token(
                        countryCode = "DE",
                        partyId = "TNM",
                        uid = "12345678905880",
                        type = TokenType.RFID,
                        contractId = "DE8ACC12E46L89",
                        visualNumber = "DF000-2001-8999-1",
                        issuer = "TheNewMotion",
                        groupId = "DF000-2001-8999",
                        valid = true,
                        whitelist = WhitelistType.ALLOWED,
                        language = "it",
                        defaultProfileType = ProfileType.GREEN,
                        energyContract = EnergyContract(
                            supplierName = "Greenpeace Energy eG",
                            contractId = "0123456789",
                        ),
                        lastUpdated = Instant.parse("2018-12-10T17:25:10Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/tokens/?date_from=2019-01-28T12:00:00Z&date_to=2019-01-29T12:00:00Z"),
        )

        // then
        expectThat(slots) {
            get { dateFrom.captured }.isEqualTo(Instant.parse("2019-01-28T12:00:00Z"))
            get { dateTo.captured }.isEqualTo(Instant.parse("2019-01-29T12:00:00Z"))
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers["X-Total-Count"] }.isEqualTo("1")
            get { headers["X-Limit"] }.isEqualTo("50")
            get { body }.isJsonEqualTo(
                """
                {
                "data" : [ {
                    "country_code": "DE",
                    "party_id": "TNM",
                    "uid": "12345678905880",
                    "type": "RFID",
                    "contract_id": "DE8ACC12E46L89",
                    "visual_number": "DF000-2001-8999-1",
                    "issuer": "TheNewMotion",
                    "group_id": "DF000-2001-8999",
                    "valid": true,
                    "whitelist": "ALLOWED",
                    "language": "it",
                    "default_profile_type": "GREEN",
                    "energy_contract": {
                        "supplier_name": "Greenpeace Energy eG",
                        "contract_id": "0123456789"
                    },
                    "last_updated": "2018-12-10T17:25:10Z"
                    } ],
                    "status_code": 1000,
                    "status_message": "Success",
                    "timestamp": "2015-06-30T21:59:59Z"
                }
                """.trimIndent(),
            )
        }
    }
}

private fun TokensEmspRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        TokensEmspServer(
            service = TokensEmspService(repo),
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/tokens",
        ).registerOn(transportServer)
    }

    return transportServer.initRouterAndBuildClient()
}
