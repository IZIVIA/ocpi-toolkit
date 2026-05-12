package com.izivia.ocpi.toolkit211.modules.tokens.http.emsp

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.toSearchResult
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit211.modules.tokens.repositories.TokensEmspRepository
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.mapper
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Instant

class TokensEmspHttpGetTokensTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should list tokens`(serializer: OcpiSerializer) {
        mapper = serializer
        val srv = mockk<TokensEmspRepository> {
            coEvery { getTokens(any(), any(), any(), any()) } coAnswers {
                listOf(
                    Token(
                        uid = "012345678",
                        type = TokenType.RFID,
                        authId = "FA54320",
                        visualNumber = "DF000-2001-8999-1",
                        issuer = "TheNewMotion",
                        valid = true,
                        whitelist = WhitelistType.ALLOWED,
                        language = "nl",
                        lastUpdated = Instant.parse("2015-06-29T22:39:09Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/tokens/"),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers["X-Total-Count"] }.isEqualTo("1")
            get { body }.isNotNull().isJsonEqualTo(
                """
                {
                  "data": [
                    {
                      "uid": "012345678",
                      "type": "RFID",
                      "auth_id": "FA54320",
                      "visual_number": "DF000-2001-8999-1",
                      "issuer": "TheNewMotion",
                      "valid": true,
                      "whitelist": "ALLOWED",
                      "language": "nl",
                      "last_updated": "2015-06-29T22:39:09Z"
                    }
                  ],
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "$nowString"
                }
                """.trimIndent(),
            )
        }
    }
}
