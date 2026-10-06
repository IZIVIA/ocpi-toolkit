package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.modules.cdr.CdrsCpoClient
import com.izivia.ocpi.toolkit211.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit211.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit211.modules.locations.domain.Location
import com.izivia.ocpi.toolkit211.modules.locations.domain.LocationType
import com.izivia.ocpi.toolkit211.modules.tariff.TariffCpoClient
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit211.serialization.mapper
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.math.BigDecimal
import java.time.Instant

class AcknowledgementClientTest {
    private val timestamp = "2026-10-05T10:00:00Z"

    @TestFactory
    fun `acknowledgements ignore data and preserve peer errors`(): List<DynamicTest> =
        OcpiSerializationRegistry.serializers.flatMap { serializer ->
            val transport = mockk<TransportClient>()
            val builder = mockk<TransportClientBuilder>()
            coEvery {
                builder.buildFor(any(), any<ModuleID>(), any())
            } returns transport
            val clients: List<Pair<String, suspend () -> Unit>> = listOf(
                "CDR POST" to {
                    assertEquals(
                        "https://peer/cdr/123",
                        CdrsCpoClient(builder, "peer").postCdr(
                            Cdr(
                                id = "cdr",
                                startDateTime = Instant.parse(timestamp),
                                stopDateTime = Instant.parse(timestamp),
                                authId = "token",
                                authMethod = AuthMethod.AUTH_REQUEST,
                                location = Location(
                                    id = "location",
                                    type = LocationType.ON_STREET,
                                    address = "address",
                                    city = "city",
                                    postalCode = "00000",
                                    country = "FRA",
                                    coordinates = GeoLocation("48", "2"),
                                    lastUpdated = Instant.parse(timestamp),
                                ),
                                currency = "EUR",
                                tariffs = null,
                                chargingPeriods = emptyList(),
                                totalCost = BigDecimal.ZERO,
                                totalEnergy = BigDecimal.ZERO,
                                totalTime = BigDecimal.ZERO,
                                lastUpdated = Instant.parse(timestamp),
                            ),
                        ),
                    )
                },
                "tariff DELETE" to { TariffCpoClient(builder, "peer").deleteTariff("FR", "ABC", "tariff") },
                "credentials DELETE" to { CredentialsClient(transport).delete("token") },
            )
            clients.flatMap { (clientName, call) ->
                listOf(
                    "",
                    ",\"data\":null",
                    ",\"data\":{\"unexpected\":[1,true,\"text\"]}",
                    ",\"data\":[1,2]",
                    ",\"data\":\"text\"",
                    ",\"data\":42",
                ).flatMap { data ->
                    listOf(
                        HttpStatus.OK to 1000,
                        HttpStatus.CREATED to 1000,
                        HttpStatus.BAD_REQUEST to 2001,
                        HttpStatus.OK to 2001,
                    ).map { (httpStatus, ocpiCode) ->
                        DynamicTest.dynamicTest(
                            "${serializer.name}: $clientName HTTP ${httpStatus.code} OCPI $ocpiCode $data",
                        ) {
                            val previous = mapper
                            try {
                                mapper = serializer
                                coEvery { transport.send(any()) } returns HttpResponse(
                                    httpStatus,
                                    """
                                    {
                                      "status_code":$ocpiCode,
                                      "status_message":"Peer message",
                                      "timestamp":"$timestamp"$data
                                    }
                                    """.trimIndent(),
                                    headers = mapOf("Location" to "https://peer/cdr/123"),
                                )
                                if (ocpiCode == 1000) {
                                    runBlocking { call() }
                                } else {
                                    val error = assertThrows(OcpiException::class.java) { runBlocking { call() } }
                                    assertEquals(2001, error.ocpiStatusCode)
                                    assertEquals(httpStatus, error.httpStatus)
                                    assertEquals("Peer message", error.message)
                                }
                            } finally {
                                mapper = previous
                            }
                        }
                    }
                } + listOf(HttpStatus.OK, HttpStatus.BAD_REQUEST).map { status ->
                    DynamicTest.dynamicTest("${serializer.name}: $clientName malformed HTTP ${status.code}") {
                        val previous = mapper
                        try {
                            mapper = serializer
                            coEvery { transport.send(any()) } returns HttpResponse(status, "not json")
                            val expected = if (status == HttpStatus.OK) {
                                OcpiToolkitResponseParsingException::class.java
                            } else {
                                HttpException::class.java
                            }
                            assertThrows(expected) { runBlocking { call() } }
                        } finally {
                            mapper = previous
                        }
                    }
                }
            }
        }
}
