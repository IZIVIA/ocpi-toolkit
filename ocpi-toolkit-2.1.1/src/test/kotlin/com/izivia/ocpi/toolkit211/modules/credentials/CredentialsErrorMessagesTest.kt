package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit.transport.domain.parseHttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit211.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit211.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit211.samples.common.toHttp4k
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.mapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.http4k.core.HttpHandler
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.util.Base64

/**
 * Exception messages are logged and returned to the partner in status_message: they must not contain the credentials
 * token. Malformed Authorization headers are covered by AuthorizationHeaderTest.
 */
class CredentialsErrorMessagesTest : TestWithSerializerProviders {

    private val token = "unknown-credentials-token-1234"
    private val encodedToken = Base64.getEncoder().encodeToString(token.toByteArray())
    private val credentialsBody =
        """{"token":"tokenB","url":"http://sender/versions","business_details":{"name":"Sender"},""" +
            """"party_id":"ABC","country_code":"FR"}"""

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `GET with an unknown token does not return the token in status_message`(serializer: OcpiSerializer) {
        mapper = serializer
        val resp = send(
            buildHttpRequest(HttpMethod.GET, "/credentials").withHeaders(authorization("Token $encodedToken")),
        )

        expectInvalidTokenResponse(resp)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `POST with an unknown token does not return the token in status_message`(serializer: OcpiSerializer) {
        mapper = serializer
        val resp = send(
            buildHttpRequest(HttpMethod.POST, "/credentials", credentialsBody)
                .withHeaders(authorization("Token $encodedToken")),
        )

        expectInvalidTokenResponse(resp)
    }

    private fun expectInvalidTokenResponse(resp: HttpResponse) {
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.BAD_REQUEST)
            get { body }.isNotNull().and {
                contains("\"status_code\":2001")
                contains("Invalid token - should be either a TokenA or a ServerToken")
                not { contains(token) }
                not { contains(encodedToken) }
            }
        }
    }

    private fun authorization(value: String) = mapOf(
        "Authorization" to value,
        "X-Request-ID" to "request-id",
        "X-Correlation-ID" to "correlation-id",
    )

    /**
     * Sends the request straight to the router: the test transport client would overwrite the Authorization header.
     */
    private fun send(request: HttpRequest): HttpResponse = buildRouter()(request.toHttp4k()).let {
        HttpResponse(status = parseHttpStatus(it.status.code), body = it.bodyString())
    }

    private fun buildRouter(): HttpHandler {
        val partnerRepository = mockk<PartnerRepository>(relaxed = true)
        coEvery { partnerRepository.getPartnerIdByCredentialsServerToken(any()) } returns null
        coEvery { partnerRepository.getPartnerIdByCredentialsTokenA(any()) } returns null
        val transportServer = Http4kTransportServer("http://localhost:1234", 1234)
        runBlocking {
            CredentialsServer(
                service = CredentialsServerService(
                    partnerRepository = partnerRepository,
                    credentialsDetailsRepository = mockk(relaxed = true),
                    transportClientBuilder = mockk(),
                    serverVersionsUrlProvider = { "http://receiver/versions" },
                    requiredEndpoints = null,
                ),
                versionsRepository = InMemoryVersionsRepository(),
                basePathOverride = "/credentials",
            ).registerOn(transportServer)
        }
        return transportServer.initRouterAndBuildClient().client
    }
}
