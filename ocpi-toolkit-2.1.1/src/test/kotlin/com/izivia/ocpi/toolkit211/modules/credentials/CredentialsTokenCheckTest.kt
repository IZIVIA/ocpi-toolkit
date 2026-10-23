package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit.transport.encodeBase64
import com.izivia.ocpi.toolkit211.common.OcpiClientMethodNotAllowedException
import com.izivia.ocpi.toolkit211.common.checkToken
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit211.modules.locations.domain.BusinessDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectCatching
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isFailure
import strikt.assertions.isSuccess

/**
 * From OCPI 2.1.1 doc: PUT and DELETE credentials "MUST return a HTTP status code 405: method not allowed if the
 * client has not been registered", i.e. a known client authenticated with its CREDENTIALS_TOKEN_A. A token that
 * matches no party is answered 401.
 */
class CredentialsTokenCheckTest {

    private val unknownToken = "unknown-token"
    private val tokenA = "token-a"
    private val serverToken = "server-token"

    private val partnerRepository = mockk<PartnerRepository>(relaxed = true) {
        coEvery { isCredentialsTokenAValid(any()) } answers { firstArg<String>() == tokenA }
        coEvery { isCredentialsServerTokenValid(any()) } answers { firstArg<String>() == serverToken }
        coEvery { getPartnerIdByCredentialsServerToken(any()) } answers {
            if (firstArg<String>() == serverToken) "partner" else null
        }
    }

    private val service = CredentialsServerService(
        partnerRepository = partnerRepository,
        credentialsDetailsRepository = mockk(),
        transportClientBuilder = mockk(),
        serverVersionsUrlProvider = { "http://server/versions" },
        requiredEndpoints = null,
    )

    private fun request(method: HttpMethod, token: String) = HttpRequest(
        method = method,
        path = "/2.1.1/credentials",
        headers = mapOf("Authorization" to "Token ${token.encodeBase64()}"),
    )

    private suspend fun handle(method: HttpMethod, token: String) {
        partnerRepository.checkToken(request(method, token))
        when (method) {
            HttpMethod.PUT -> service.put(token, credentials(token), emptyMap())
            else -> service.delete(token)
        }
    }

    private fun credentials(token: String) = Credentials(
        token = token,
        url = "http://client/versions",
        businessDetails = BusinessDetails(name = "client"),
        partyId = "CLI",
        countryCode = "FR",
    )

    @ParameterizedTest
    @EnumSource(value = HttpMethod::class, names = ["PUT", "DELETE"])
    fun `unknown token is answered 401`(method: HttpMethod) {
        expectCatching { handle(method, unknownToken) }
            .isFailure()
            .isA<HttpException>()
            .get { status }
            .isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod::class, names = ["PUT", "DELETE"])
    fun `token A of a client not registered yet is answered 405`(method: HttpMethod) {
        expectCatching { handle(method, tokenA) }
            .isFailure()
            .isA<OcpiClientMethodNotAllowedException>()
            .get { httpStatus }
            .isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }

    @ParameterizedTest
    @EnumSource(value = HttpMethod::class, names = ["PUT", "DELETE"])
    fun `server token of a registered client passes the token check`(method: HttpMethod) {
        expectCatching { partnerRepository.checkToken(request(method, serverToken)) }.isSuccess()
    }

    @Test
    fun `DELETE with the server token of a registered client is processed`() {
        runBlocking { handle(HttpMethod.DELETE, serverToken) }
        coVerify { partnerRepository.invalidateCredentialsClientToken("partner") }
    }
}
