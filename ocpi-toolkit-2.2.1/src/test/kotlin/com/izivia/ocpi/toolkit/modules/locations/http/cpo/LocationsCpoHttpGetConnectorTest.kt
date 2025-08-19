package com.izivia.ocpi.toolkit.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorFormat
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorType
import com.izivia.ocpi.toolkit.modules.locations.domain.PowerType
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsCpoHttpGetConnectorTest {

    @Test
    fun `should be connector`() {
        val slots = object {
            var locationId = slot<String>()
            var evseUid = slot<String>()
            var connectorId = slot<String>()
        }
        val srv = mockk<LocationsCpoInterface> {
            coEvery {
                getConnector(
                    capture(slots.locationId),
                    capture(slots.evseUid),
                    capture(slots.connectorId),
                )
            } coAnswers {
                Connector(
                    id = "1",
                    standard = ConnectorType.IEC_62196_T2,
                    format = ConnectorFormat.CABLE,
                    powerType = PowerType.AC_3_PHASE,
                    maxVoltage = 220,
                    maxAmperage = 16,
                    tariffIds = listOf("11"),
                    lastUpdated = Instant.parse("2015-03-16T10:10:02Z"),
                )
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/LOC1/3256/1"),
        )

        // then
        expectThat(slots) {
            get { locationId.captured }.isEqualTo("LOC1")
            get { evseUid.captured }.isEqualTo("3256")
            get { connectorId.captured }.isEqualTo("1")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                    {
                      "data": {
                            "id": "1",
                            "standard": "IEC_62196_T2",
                            "format": "CABLE",
                            "power_type": "AC_3_PHASE",
                            "max_voltage": 220,
                            "max_amperage": 16,
                            "tariff_ids": ["11"],
                            "last_updated": "2015-03-16T10:10:02Z"
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
