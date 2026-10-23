package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit.transport.AbstractTransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.OcpiResponseBody
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.modules.tokens.domain.AllowedType
import com.izivia.ocpi.toolkit211.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeOcpiResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.time.Instant

class TokensCpoClientPostTokenTest : TestWithSerializerProviders {

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `postToken without location references sends no body and no content type`(serializer: OcpiSerializer) {
        mapper = serializer
        val sent = mutableListOf<HttpRequest>()
        val transport = object : AbstractTransportClient("token") {
            override suspend fun doSend(request: HttpRequest): HttpResponse {
                sent += request
                return HttpResponse(
                    HttpStatus.OK,
                    mapper.serializeOcpiResponse(
                        OcpiResponseBody(
                            data = AuthorizationInfo(AllowedType.ALLOWED),
                            statusCode = 1000,
                            timestamp = Instant.parse("2026-01-01T00:00:00Z"),
                        ),
                    ),
                )
            }
        }
        val builder = mockk<TransportClientBuilder>()
        coEvery { builder.buildFor("partner", ModuleID.tokens, any()) } returns transport

        val result = runBlocking {
            TokensCpoClient(builder, "partner").postToken("uid", null, null)
        }

        expectThat(result.allowed).isEqualTo(AllowedType.ALLOWED)
        val request = expectThat(sent.singleOrNull()).isNotNull().subject
        expectThat(request.method).isEqualTo(HttpMethod.POST)
        expectThat(request.path).isEqualTo("/uid/authorize")
        expectThat(request.body).isNull()
        expectThat(request.getHeader(HttpHeaders.CONTENT_TYPE)).isNull()
    }
}
