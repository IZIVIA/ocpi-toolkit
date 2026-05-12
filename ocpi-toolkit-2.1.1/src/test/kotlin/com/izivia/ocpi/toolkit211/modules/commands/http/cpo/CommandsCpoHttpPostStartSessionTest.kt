package com.izivia.ocpi.toolkit211.modules.commands.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.commands.CommandCpoInterface
import com.izivia.ocpi.toolkit211.modules.commands.domain.CommandResponse
import com.izivia.ocpi.toolkit211.modules.commands.domain.CommandResponseType
import com.izivia.ocpi.toolkit211.modules.commands.domain.StartSession
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.deserializeOcpiResponse
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class CommandsCpoHttpPostStartSessionTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should post start session and return ACCEPTED`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var startSession = slot<StartSession>()
        }
        val srv = mockk<CommandCpoInterface> {
            coEvery { postStartSession(any(), capture(slots.startSession)) } coAnswers {
                CommandResponse(
                    result = CommandResponseType.ACCEPTED,
                    timeout = 30,
                    message = null,
                )
            }
        }.buildServer()

        val token = Token(
            uid = "012345678",
            type = TokenType.RFID,
            authId = "FA54320",
            issuer = "TheNewMotion",
            valid = true,
            whitelist = WhitelistType.ALLOWED,
            lastUpdated = Instant.parse("2015-06-29T22:39:09Z"),
        )

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(
                HttpMethod.POST,
                "/commands/START_SESSION",
                body = mapper.serializeObject(
                    StartSession(
                        token = token,
                        locationId = "LOC1",
                        evseUid = null,
                        connectorId = null,
                    ),
                ),
            ),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
        }
        val ocpiResponse = mapper.deserializeOcpiResponse<CommandResponse>(resp.body)
        val commandResponse = ocpiResponse.data!!
        expectThat(commandResponse) {
            get { result }.isEqualTo(CommandResponseType.ACCEPTED)
            get { timeout }.isEqualTo(30)
        }
    }
}
