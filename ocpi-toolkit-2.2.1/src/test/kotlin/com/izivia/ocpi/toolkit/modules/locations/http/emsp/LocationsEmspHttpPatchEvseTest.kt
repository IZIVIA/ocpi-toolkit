package com.izivia.ocpi.toolkit.modules.locations.http.emsp

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.json.compare.CompareMode
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Instant

class LocationsEmspHttpPatchEvseTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should patch evse`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
            var evseUid = slot<String>()
            var evse = slot<EvsePartial>()
        }
        val srv = mockk<LocationsEmspRepository> {
            coEvery {
                patchEvse(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                    capture(slots.evseUid),
                    capture(slots.evse),
                )
            } coAnswers {
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
        val evse = EvsePartial(
            uid = "1",
            evseId = null,
            status = null,
            statusSchedule = null,
            capabilities = null,
            connectors = null,
            floorLevel = null,
            coordinates = null,
            physicalReference = null,
            directions = null,
            parkingRestrictions = null,
            images = null,
            lastUpdated = null,
        )
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.PATCH, "/locations/BE/BEC/LOC1/3256", mapper.serializeObject(evse)),
        )

        // then
        expectThat(slots) {
            get { countryCode.captured }.isEqualTo("BE")
            get { partyId.captured }.isEqualTo("BEC")
            get { locationId.captured }.isEqualTo("LOC1")
            get { evseUid.captured }.isEqualTo("3256")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isNotNull().isJsonEqualTo(
                """
                {
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "2015-06-30T21:59:59Z"
                }
                """.trimIndent(),
                CompareMode.REGEX_DISABLED,
            )
        }
    }
}
