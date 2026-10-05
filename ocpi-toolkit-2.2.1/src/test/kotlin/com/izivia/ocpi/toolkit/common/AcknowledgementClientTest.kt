package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.cdr.CdrsCpoClient
import com.izivia.ocpi.toolkit.modules.chargingProfiles.ChargingProfilesCpoClient
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.*
import com.izivia.ocpi.toolkit.modules.commands.CommandCpoClient
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResult
import com.izivia.ocpi.toolkit.modules.commands.domain.CommandResultType
import com.izivia.ocpi.toolkit.modules.credentials.CredentialsClient
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.hubclientinfo.HubClientInfoSenderClient
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ConnectionStatus
import com.izivia.ocpi.toolkit.modules.tariff.TariffCpoClient
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.OcpiSerializationRegistry
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.Instant

class AcknowledgementClientTest {
    private val timestamp = "2026-10-05T10:00:00Z"

    @TestFactory
    fun `acknowledgements ignore data and preserve peer errors`(): List<DynamicTest> =
        OcpiSerializationRegistry.serializers.flatMap { serializer ->
            val transport = mockk<TransportClient>()
            val builder = mockk<TransportClientBuilder>()
            coEvery {
                builder.buildFor(any(), any<ModuleID>(), any(), any())
            } returns transport
            coEvery { builder.buildFor(any(), any<String>(), any()) } returns transport
            val profiles = ChargingProfilesCpoClient(builder, "peer", "https://peer/callback")
            val clients: List<Pair<String, suspend () -> Unit>> = listOf(
                "command callback" to {
                    CommandCpoClient(builder).postCommandCallback(
                        CommandResult(CommandResultType.ACCEPTED, null),
                        "peer",
                        "https://peer/callback",
                    )
                },
                "active profile callback" to {
                    profiles.postCallbackActiveChargingProfile(
                        "/callback",
                        ActiveChargingProfileResult(ChargingProfileResultType.ACCEPTED),
                    )
                },
                "profile callback" to {
                    profiles.postCallbackChargingProfile(
                        "/callback",
                        ChargingProfileResult(ChargingProfileResultType.ACCEPTED),
                    )
                },
                "clear profile callback" to {
                    profiles.postCallbackClearProfile(
                        "/callback",
                        ClearProfileResult(ChargingProfileResultType.ACCEPTED),
                    )
                },
                "active profile PUT" to {
                    profiles.putActiveChargingProfile(
                        "session",
                        ActiveChargingProfile(
                            Instant.parse(timestamp),
                            ChargingProfile(
                                chargingRateUnit = ChargingRateUnit.W,
                                minChargingRate = null,
                                chargingProfilePeriod = null,
                            ),
                        ),
                    )
                },
                "CDR POST" to {
                    assertEquals("https://peer/cdr/123", CdrsCpoClient(builder, "peer").postCdr(MappingData.cdr))
                },
                "tariff DELETE" to { TariffCpoClient(builder, "peer").deleteTariff("FR", "ABC", "tariff") },
                "credentials DELETE" to { CredentialsClient(transport).delete("token") },
                "ClientInfo PUT" to {
                    HubClientInfoSenderClient(builder, "peer").put(
                        "FR",
                        "ABC",
                        ClientInfo("FR", "ABC", Role.CPO, ConnectionStatus.CONNECTED, Instant.parse(timestamp)),
                    )
                },
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
