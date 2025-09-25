package com.izivia.ocpi.toolkit.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Instant

class LocationsCpoHttpGetEvseTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should be evse`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var locationId = slot<String>()
            var evseUid = slot<String>()
        }
        val srv = mockk<LocationsCpoInterface> {
            coEvery { getEvse(capture(slots.locationId), capture(slots.evseUid)) } coAnswers {
                Evse(
                    uid = "3256",
                    evseId = "BE*BEC*E041503001",
                    status = Status.AVAILABLE,
                    capabilities = listOf(Capability.RESERVABLE),
                    connectors = listOf(
                        Connector(
                            id = "1",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.CABLE,
                            powerType = PowerType.AC_3_PHASE,
                            maxVoltage = 220,
                            maxAmperage = 16,
                            tariffIds = listOf("11"),
                            lastUpdated = Instant.parse("2015-03-16T10:10:02Z"),
                        ),
                        Connector(
                            id = "2",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.SOCKET,
                            powerType = PowerType.AC_3_PHASE,
                            maxVoltage = 220,
                            maxAmperage = 16,
                            tariffIds = listOf("13"),
                            lastUpdated = Instant.parse("2015-03-18T08:12:01Z"),
                        ),
                    ),
                    floorLevel = "-1",
                    physicalReference = "1",
                    lastUpdated = Instant.parse("2015-06-28T08:12:01Z"),
                )
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/LOC1/3256"),
        )

        // then
        expectThat(slots) {
            get { locationId.captured }.isEqualTo("LOC1")
            get { evseUid.captured }.isEqualTo("3256")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isNotNull().isJsonEqualTo(
                """
                {
                  "data": {
                        "uid": "3256",
                        "evse_id": "BE*BEC*E041503001",
                        "status": "AVAILABLE",
                        "capabilities": [
                            "RESERVABLE"
                        ],
                        "connectors": [
                              {
                                "id": "1",
                                "standard": "IEC_62196_T2",
                                "format": "CABLE",
                                "power_type": "AC_3_PHASE",
                                "max_voltage": 220,
                                "max_amperage": 16,
                                "tariff_ids": ["11"],
                                "last_updated": "2015-03-16T10:10:02Z"
                              },
                              {
                                "id": "2",
                                "standard": "IEC_62196_T2",
                                "format": "SOCKET",
                                "power_type": "AC_3_PHASE",
                                "max_voltage": 220,
                                "max_amperage": 16,
                                "tariff_ids": ["13"],
                                "last_updated": "2015-03-18T08:12:01Z"
                              }
                        ],
                        "physical_reference": "1",
                        "floor_level": "-1",
                        "last_updated": "2015-06-28T08:12:01Z"
                  },
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "$nowString"
                }
                """.trimIndent(),
            )
        }
    }
}
