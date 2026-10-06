package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.common.OcpiException
import com.izivia.ocpi.toolkit.common.OcpiToolkitResponseParsingException
import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ConnectionStatus
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class HubClientInfoSenderClientTest : TestWithSerializerProviders {
    private val clientInfo = ClientInfo(
        "FR",
        "ABC",
        Role.EMSP,
        ConnectionStatus.CONNECTED,
        Instant.parse("2026-01-01T00:00:00Z"),
    )

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `put accepts successful acknowledgements with absent or null data`(serializer: OcpiSerializer) {
        mapper = serializer
        for (status in listOf(HttpStatus.OK, HttpStatus.CREATED)) {
            for (data in listOf("", "\"data\":null,")) {
                val (client, transport) = client(HttpResponse(status, responseBody(1000, data)))

                runBlocking { client.put("FR", "ABC", clientInfo) }

                coVerify(exactly = 1) {
                    transport.send(match { it.method == HttpMethod.PUT && it.path == "/FR/ABC" })
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `put preserves OCPI errors for both successful and failed HTTP statuses`(serializer: OcpiSerializer) {
        mapper = serializer
        for (status in listOf(HttpStatus.OK, HttpStatus.BAD_REQUEST)) {
            val (client, _) = client(HttpResponse(status, responseBody(2001)))

            val error = assertThrows<OcpiException> {
                runBlocking { client.put("FR", "ABC", clientInfo) }
            }

            expectThat(error.ocpiStatusCode).isEqualTo(2001)
            expectThat(error.httpStatus).isEqualTo(status)
        }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `put rejects malformed successful responses`(serializer: OcpiSerializer) {
        mapper = serializer
        val (client, _) = client(HttpResponse(HttpStatus.OK, "not JSON"))

        assertThrows<OcpiToolkitResponseParsingException> {
            runBlocking { client.put("FR", "ABC", clientInfo) }
        }
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `put preserves HTTP errors without an OCPI body`(serializer: OcpiSerializer) {
        mapper = serializer
        val (client, _) = client(HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, "not JSON"))

        assertThrows<HttpException> {
            runBlocking { client.put("FR", "ABC", clientInfo) }
        }
    }

    private fun responseBody(statusCode: Int, data: String = "") =
        """{$data"status_code":$statusCode,"timestamp":"2026-01-01T00:00:00Z"}"""

    private fun client(response: HttpResponse): Pair<HubClientInfoSenderClient, TransportClient> {
        val transport = mockk<TransportClient>()
        val builder = mockk<TransportClientBuilder>()
        coEvery { builder.buildFor("partner", ModuleID.hubclientinfo, InterfaceRole.RECEIVER) } returns transport
        coEvery { transport.send(any()) } returns response
        return HubClientInfoSenderClient(builder, "partner") to transport
    }
}
