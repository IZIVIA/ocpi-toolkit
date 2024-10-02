package com.izivia.ocpi.toolkit.modules.tokens.http.emsp

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
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

class TokensEmspHttpPostTokenTest {
    @Test
    fun `should post token with empty location reference`() {
        val slots = object {
            var tokenUID = slot<String>()
            var type = slot<TokenType>()
        }
        val srv = mockk<TokensEmspRepository> {
            coEvery {
                postToken(capture(slots.tokenUID), capture(slots.type), null)
            } coAnswers {
                AuthorizationInfo(
                    allowed = AllowedType.ALLOWED,
                    token = Token(
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
                    location = null,
                    authorizationReference = null,
                    info = null,
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.POST, "/tokens/012345678/authorize?type=RFID"),
        )

        // then
        expectThat(slots) {
            get { tokenUID.captured }.isEqualTo("012345678")
            get { type.captured }.isEqualTo(TokenType.RFID)
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                {
                "data" : {
                    "allowed" : "ALLOWED",
                    "token" : {
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
                    }
                    },
                    "status_code": 1000,
                    "status_message": "Success",
                    "timestamp": "2015-06-30T21:59:59Z"
                }
                """.trimIndent(),
            )
        }
    }

    @Test
    fun `should post token with location reference`() {
        val slots = object {
            var tokenUID = slot<String>()
            var type = slot<TokenType>()
            var locationReferences = slot<LocationReferences>()
        }
        val srv = mockk<TokensEmspRepository> {
            coEvery {
                postToken(capture(slots.tokenUID), capture(slots.type), capture(slots.locationReferences))
            } coAnswers {
                AuthorizationInfo(
                    allowed = AllowedType.ALLOWED,
                    token = Token(
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
                    location = null,
                    authorizationReference = null,
                    info = null,
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val locationReferences = LocationReferences(
            locationId = "LOC1",
            evseUids = listOf("BE*BEC*E041503001", "BE*BEC*E041503002"),
        )
        val resp: HttpResponse = srv.send(
            buildHttpRequest(
                HttpMethod.POST,
                "/tokens/012345678/authorize?type=RFID",
                mapper.writeValueAsString(locationReferences),
            ),
        )

        // then
        expectThat(slots) {
            get { tokenUID.captured }.isEqualTo("012345678")
            get { type.captured }.isEqualTo(TokenType.RFID)
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                {
                "data" : {
                    "allowed" : "ALLOWED",
                    "token" : {
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
                    }
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
