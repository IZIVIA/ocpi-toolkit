package com.izivia.ocpi.toolkit.modules.tokens.http.cpo

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Instant

class TokensCpoHttpPatchTokenTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should patch token`(serializer: OcpiSerializer) {
        mapper = serializer
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

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.PATCH, "/tokens/DE/TNM/012345678/?type=RFID", mapper.serializeObject(token)),
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
            get { body }.isNotNull().isJsonEqualTo(
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

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should patch partial token`(serializer: OcpiSerializer) {
        mapper = serializer
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

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(
                HttpMethod.PATCH,
                "/tokens/DE/TNM/012345678/?type=RFID",
                mapper.serializeObject(partialToken),
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
            get { body }.isNotNull().isJsonEqualTo(
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
