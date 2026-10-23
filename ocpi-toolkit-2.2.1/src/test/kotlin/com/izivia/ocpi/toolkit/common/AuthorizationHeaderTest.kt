package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import java.time.Instant

class AuthorizationHeaderTest {
    private val now = Instant.parse("2024-01-15T10:30:00Z")

    private fun respond(headers: Map<String, String>): HttpResponse = runBlocking {
        val request = HttpRequest(HttpMethod.GET, headers = headers)
        request.respondObject(now) { request.parseAuthorizationHeader() }
    }

    private fun expectUnauthorized(headers: Map<String, String>) {
        expectThat(respond(headers)) {
            get { status.code }.isEqualTo(401)
            get { this.headers["WWW-Authenticate"] }.isEqualTo("Token")
        }
    }

    @Test
    fun `missing authorization header is answered 401`() {
        expectUnauthorized(emptyMap())
    }

    @Test
    fun `authorization header without Token prefix is answered 401`() {
        expectUnauthorized(mapOf(Header.AUTHORIZATION to "Bearer abc"))
    }

    @Test
    fun `authorization header with non Base64 token is answered 401`() {
        expectUnauthorized(mapOf(Header.AUTHORIZATION to "Token %%%"))
    }

    @Test
    fun `error message does not contain the authorization header value`() {
        listOf("Bearer secret-value", "Token secret%%%value").forEach { value ->
            val e = assertThrows<HttpException> {
                HttpRequest(HttpMethod.GET, headers = mapOf(Header.AUTHORIZATION to value)).parseAuthorizationHeader()
            }
            expectThat(e.message.orEmpty()).not().contains("secret")
        }
    }
}
