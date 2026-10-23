package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.OcpiClientMethodNotAllowedException
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit211.modules.locations.domain.BusinessDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

/**
 * From OCPI 2.1.1 doc (credentials, POST): "This must return a HTTP status code 405: method not allowed if the
 * client was already registered."
 */
class CredentialsServerServicePostTest {

    private val versionsBody =
        """{"data":[{"version":"2.1.1","url":"http://sender/2.1.1"}],"status_code":1000,""" +
            """"timestamp":"2026-01-01T00:00:00Z"}"""
    private val versionDetailsBody =
        """{"data":{"version":"2.1.1","endpoints":[]},"status_code":1000,"timestamp":"2026-01-01T00:00:00Z"}"""

    private val credentials = Credentials(
        token = "newClientToken",
        url = "http://sender/versions",
        businessDetails = BusinessDetails(name = "Sender"),
        partyId = "ABC",
        countryCode = "FR",
    )

    private val partnerRepository = mockk<PartnerRepository>(relaxed = true).also {
        coEvery { it.getPartnerIdByCredentialsServerToken(any()) } returns null
        coEvery { it.getPartnerIdByCredentialsTokenA(any()) } returns null
        coEvery { it.saveCredentialsServerToken(any(), any()) } returns "newServerToken"
    }

    private val service = CredentialsServerService(
        partnerRepository = partnerRepository,
        credentialsDetailsRepository = mockk(relaxed = true),
        transportClientBuilder = transportClientBuilder(),
        serverVersionsUrlProvider = { "http://receiver/versions" },
        requiredEndpoints = null,
    )

    @Test
    fun `post with the server token of a registered client returns 405`() {
        coEvery { partnerRepository.getPartnerIdByCredentialsServerToken("serverToken") } returns "partner"
        coEvery { partnerRepository.getCredentialsClientToken("partner") } returns "clientToken"

        expectThrows<OcpiClientMethodNotAllowedException> {
            service.post(token = "serverToken", credentials = credentials, debugHeaders = emptyMap())
        }
        coVerify(exactly = 0) { partnerRepository.saveCredentialsClientToken(any(), any()) }
        coVerify(exactly = 0) { partnerRepository.saveCredentialsServerToken(any(), any()) }
    }

    @Test
    fun `post with the server token of an unregistered client registers it again`() {
        coEvery { partnerRepository.getPartnerIdByCredentialsServerToken("serverToken") } returns "partner"
        coEvery { partnerRepository.getCredentialsClientToken("partner") } returns null

        val result = runBlocking {
            service.post(token = "serverToken", credentials = credentials, debugHeaders = emptyMap())
        }

        expectThat(result.token).isEqualTo("newServerToken")
        coVerify { partnerRepository.saveCredentialsClientToken("partner", "newClientToken") }
    }

    @Test
    fun `post with token A registers the client`() {
        coEvery { partnerRepository.getPartnerIdByCredentialsTokenA("tokenA") } returns "partner"

        val result = runBlocking {
            service.post(token = "tokenA", credentials = credentials, debugHeaders = emptyMap())
        }

        expectThat(result.token).isEqualTo("newServerToken")
        coVerify { partnerRepository.saveCredentialsClientToken("partner", "newClientToken") }
        coVerify { partnerRepository.invalidateCredentialsTokenA("partner") }
    }

    private fun transportClientBuilder(): TransportClientBuilder {
        val builder = mockk<TransportClientBuilder>()
        coEvery { builder.build(any(), any()) } answers {
            val body = if (firstArg<String>() == "http://sender/versions") versionsBody else versionDetailsBody
            mockk<TransportClient> { coEvery { send(any()) } returns HttpResponse(HttpStatus.OK, body) }
        }
        return builder
    }
}
