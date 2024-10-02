package com.izivia.ocpi.toolkit.modules.tokens.http.cpo

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.EnergyContract
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensCpoService
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

class TokensCpoHttpGetTokenTest {
    @Test
    fun `should get token`() {
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var tokenUID = slot<String>()
            var type = slot<TokenType>()
        }
        val srv = mockk<TokensCpoRepository> {
            coEvery {
                getToken(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.tokenUID),
                    capture(slots.type),
                )
            } coAnswers {
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
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/tokens/DE/TNM/012345678/?type=RFID"),
        )

        // then
        expectThat(slots) {
            get { countryCode.captured }.isEqualTo("DE")
            get { partyId.captured }.isEqualTo("TNM")
            get { tokenUID.captured }.isEqualTo("012345678")
            get { type.captured }.isEqualTo(TokenType.RFID)
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                {
                "data" : {
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
                    },
                    "status_code": 1000,
                    "status_message": "Success",
                    "timestamp": "2015-06-30T21:59:59Z"
                }
                """.trimIndent(),
            )
        }
    }
}

private fun TokensCpoRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        TokensCpoServer(
            service = TokensCpoService(repo),
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/tokens",
        ).registerOn(transportServer)
    }

    return transportServer.initRouterAndBuildClient()
}
