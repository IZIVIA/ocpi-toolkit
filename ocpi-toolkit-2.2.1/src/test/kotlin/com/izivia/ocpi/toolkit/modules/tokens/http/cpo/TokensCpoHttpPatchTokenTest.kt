package com.izivia.ocpi.toolkit.modules.tokens.http.cpo

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoServer
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
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

class TokensCpoHttpPatchTokenTest {
    @Test
    fun `should patch token`() {
        val token = Token(
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
        val slots = object {
            var token = slot<TokenPartial>()
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var tokenUID = slot<String>()
            var type = slot<TokenType>()
        }
        val srv = mockk<TokensCpoRepository> {
            coEvery {
                patchToken(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.tokenUID),
                    capture(slots.type),
                    capture(slots.token),
                )
            } coAnswers { token }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.PATCH, "/tokens/DE/TNM/012345678/?type=RFID", mapper.writeValueAsString(token)),
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
                    "status_code": 1000,
                    "status_message": "Success",
                    "timestamp": "2015-06-30T21:59:59Z"
                }
                """.trimIndent(),
            )
        }
    }

    @Test
    fun `should patch partial token`() {
        val token = Token(
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

        val partialToken = TokenPartial(
            countryCode = "DE",
            partyId = "TNM",
            uid = "12345678905880",
            type = TokenType.RFID,
            contractId = "DE8ACC12E46L89",
            visualNumber = "DF000-2001-8999-1",
            // issuer = "TheNewMotion",
            issuer = null,
            groupId = "DF000-2001-8999",
            valid = true,
            whitelist = WhitelistType.ALLOWED,
            // language = "it",
            language = null,
            defaultProfileType = ProfileType.GREEN,
            energyContract = EnergyContractPartial(
                supplierName = "Greenpeace Energy eG",
                contractId = "0123456789",
            ),
            lastUpdated = Instant.parse("2018-12-10T17:25:10Z"),
        )

        val slots = object {
            var token = slot<TokenPartial>()
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var tokenUID = slot<String>()
            var type = slot<TokenType>()
        }
        val srv = mockk<TokensCpoRepository> {
            coEvery {
                patchToken(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.tokenUID),
                    capture(slots.type),
                    capture(slots.token),
                )
            } coAnswers { token }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(
                HttpMethod.PATCH,
                "/tokens/DE/TNM/012345678/?type=RFID",
                mapper.writeValueAsString(partialToken),
            ),
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
