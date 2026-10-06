package com.izivia.ocpi.toolkit.modules.tokens.http.emsp

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoClient
import com.izivia.ocpi.toolkit.modules.locations.domain.EvsePartial
import com.izivia.ocpi.toolkit.modules.locations.domain.Status
import com.izivia.ocpi.toolkit.modules.locations.http.emsp.MockHttpTransportClientBuilder
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspClient
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import kotlinx.coroutines.runBlocking
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

/**
 * An OCPI identifier is printable ASCII: it may hold a space, a slash or a percent sign. A client puts it
 * in the request path as one encoded segment, never raw, or the request breaks or reaches another path.
 */
class TokensEmspClientPathEncodingTest : TestWithSerializerProviders {
    private val ack = """{"status_code":1000,"status_message":"Success","timestamp":"2015-06-30T21:59:59Z"}"""

    private fun recording(paths: MutableList<String>) =
        MockHttpTransportClientBuilder { request: Request ->
            paths += request.uri.path
            Response(OK).body(ack)
        }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `a token uid is encoded as one path segment`(serializer: OcpiSerializer) {
        mapper = serializer
        val paths = mutableListOf<String>()
        val client = TokensEmspClient(recording(paths), "partner")
        val token = Token(
            countryCode = "FR",
            partyId = "LAB",
            uid = "04 AB/1%",
            type = TokenType.RFID,
            contractId = "FR-LAB-C12345678",
            issuer = "lab",
            valid = true,
            whitelist = WhitelistType.ALLOWED,
            lastUpdated = Instant.parse("2015-06-30T21:59:59Z"),
        )

        runBlocking { client.putToken("FR", "LAB", "04 AB/1%", TokenType.RFID, token) }

        expectThat(paths).isEqualTo(mutableListOf("/FR/LAB/04%20AB%2F1%25"))
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `location identifiers are encoded as path segments`(serializer: OcpiSerializer) {
        mapper = serializer
        val paths = mutableListOf<String>()
        val client = LocationsCpoClient(recording(paths), "partner")

        runBlocking {
            client.patchEvse(
                "FR",
                "LAB",
                "LOC 1",
                "FR*LAB*E1/2",
                EvsePartial(status = Status.AVAILABLE, lastUpdated = Instant.parse("2015-06-30T21:59:59Z")),
            )
        }

        expectThat(paths).isEqualTo(mutableListOf("/FR/LAB/LOC%201/FR*LAB*E1%2F2"))
    }
}
