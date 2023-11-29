package com.izivia.ocpi.toolkit.modules.locations.http.emsp

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsEmspServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsEmspService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsEmspHttpPatchEvseTest {
    @Test
    fun `should patch evse`() {
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
            var evseUid = slot<String>()
            var evse = slot<EvsePartial>()
        }
        val srv = mockk<LocationsEmspRepository>() {
            coEvery {
                patchEvse(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                    capture(slots.evseUid),
                    capture(slots.evse)
                )
            } coAnswers {
                Evse(
                    uid = "3256",
                    evse_id = "BE*BEC*E041503001",
                    status = Status.AVAILABLE,
                    capabilities = listOf(Capability.RESERVABLE),
                    connectors = listOf(
                        Connector(
                            id = "1",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.CABLE,
                            power_type = PowerType.AC_3_PHASE,
                            max_voltage = 220,
                            max_amperage = 16,
                            tariff_ids = listOf("11"),
                            last_updated = Instant.parse("2015-03-16T10:10:02Z")
                        ),
                        Connector(
                            id = "2",
                            standard = ConnectorType.IEC_62196_T2,
                            format = ConnectorFormat.SOCKET,
                            power_type = PowerType.AC_3_PHASE,
                            max_voltage = 220,
                            max_amperage = 16,
                            tariff_ids = listOf("13"),
                            last_updated = Instant.parse("2015-03-18T08:12:01Z")
                        ),
                    ),
                    floor_level = "-1",
                    physical_reference = "1",
                    last_updated = Instant.parse("2015-06-28T08:12:01Z")
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val evse = EvsePartial(
            uid = "1",
            evse_id = null,
            status = null,
            status_schedule = null,
            capabilities = null,
            connectors = null,
            floor_level = null,
            coordinates = null,
            physical_reference = null,
            directions = null,
            parking_restrictions = null,
            images = null,
            last_updated = null,
        )
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.PATCH, "/locations/BE/BEC/LOC1/3256", mapper.writeValueAsString(evse))
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
            get { body }.isJsonEqualTo(
                """
                {
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "2015-06-30T21:59:59Z"
                }
                 """.trimIndent()
            )
        }
    }
}

private fun LocationsEmspRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        LocationsEmspServer(
            service = LocationsEmspService(repo),
            basePath = "/locations"
        ).registerOn(transportServer)
    }

    return transportServer.initRouterAndBuildClient()
}

