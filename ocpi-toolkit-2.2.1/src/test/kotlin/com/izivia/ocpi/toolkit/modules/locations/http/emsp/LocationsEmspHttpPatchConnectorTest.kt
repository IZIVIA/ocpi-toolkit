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

class LocationsEmspHttpPatchConnectorTest {
    @Test
    fun `should patch connector`() {
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
            var evseUid = slot<String>()
            var connectorId = slot<String>()
            var connector = slot<ConnectorPartial>()
        }
        val srv = mockk<LocationsEmspRepository>() {
            coEvery {
                patchConnector(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                    capture(slots.evseUid),
                    capture(slots.connectorId),
                    capture(slots.connector)
                )
            } coAnswers {
                Connector(
                    id = "1",
                    standard = ConnectorType.IEC_62196_T2,
                    format = ConnectorFormat.CABLE,
                    power_type = PowerType.AC_3_PHASE,
                    max_voltage = 220,
                    max_amperage = 16,
                    tariff_ids = listOf("11"),
                    last_updated = Instant.parse("2015-03-16T10:10:02Z")
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val connector = ConnectorPartial(
            id = "1",
            standard = null,
            format = null,
            power_type = null,
            max_voltage = null,
            max_amperage = null,
            tariff_ids = null,
            max_electric_power = null,
            terms_and_conditions = null,
            last_updated = null
        )
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.PATCH, "/locations/BE/BEC/LOC1/3256/1", mapper.writeValueAsString(connector))
        )

        // then
        expectThat(slots) {
            get { countryCode.captured }.isEqualTo("BE")
            get { partyId.captured }.isEqualTo("BEC")
            get { locationId.captured }.isEqualTo("LOC1")
            get { evseUid.captured }.isEqualTo("3256")
            get { connectorId.captured }.isEqualTo("1")
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
