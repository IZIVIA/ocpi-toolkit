package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly

class CdrsCpoClientTest {
    @Test
    fun `getCdr sends a GET to the exact Location URL`() {
        val location = "https://www.server.com/ocpi/emsp/2.2.1/cdrs/123456"
        val calledUrls = mutableListOf<String>()
        val builder = mockk<TransportClientBuilder>()
        coEvery { builder.buildFor("partner", any<String>(), any()) } answers {
            val baseUrl = secondArg<String>()
            object : TransportClient {
                override suspend fun send(request: HttpRequest): HttpResponse {
                    if (request.method == HttpMethod.GET) calledUrls.add(baseUrl + request.path)
                    return HttpResponse(HttpStatus.NOT_FOUND, "")
                }
            }
        }

        runBlocking { CdrsCpoClient(builder, "partner").getCdr(location) }

        expectThat(calledUrls).containsExactly(location)
    }
}
