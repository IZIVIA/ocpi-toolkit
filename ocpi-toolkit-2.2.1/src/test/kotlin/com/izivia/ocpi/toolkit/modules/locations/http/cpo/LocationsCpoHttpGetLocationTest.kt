package com.izivia.ocpi.toolkit.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsCpoService
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsCpoHttpGetLocationTest {
    @Test
    fun `should be location`() {
        val slots = object {
            var locationId = slot<String>()
        }
        val srv = mockk<LocationsCpoRepository>() {
            every { getLocation(capture((slots.locationId))) } answers {
                Location(
                    country_code = "BE",
                    party_id = "BEC",
                    id = "LOC1",
                    name = "Gent Zuid",
                    address = "F.Rooseveltlaan 3A",
                    city = "Gent",
                    postal_code = "9000",
                    country = "BEL",
                    coordinates = GeoLocation("51.047599", "3.729944"),
                    parking_type = ParkingType.ON_STREET,
                    evses = listOf(
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
                        ),
                        Evse(
                            uid = "3257",
                            evse_id = "BE*BEC*E041503002",
                            status = Status.RESERVED,
                            capabilities = listOf(Capability.RESERVABLE),
                            connectors = listOf(
                                Connector(
                                    id = "1",
                                    standard = ConnectorType.IEC_62196_T2,
                                    format = ConnectorFormat.SOCKET,
                                    power_type = PowerType.AC_3_PHASE,
                                    max_voltage = 220,
                                    max_amperage = 16,
                                    tariff_ids = listOf("12"),
                                    last_updated = Instant.parse("2015-06-29T20:39:09Z")
                                )
                            ),
                            floor_level = "-2",
                            physical_reference = "2",
                            last_updated = Instant.parse("2015-06-29T20:39:09Z")
                        )
                    ),
                    operator = BusinessDetails(
                        name = "BeCharged"
                    ),
                    publish = true,
                    time_zone = "Europe/Brussels",
                    last_updated = Instant.parse("2015-06-29T20:39:09Z")
                )
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/LOC1")
        )

        // then
        expectThat(slots) {
            get { locationId.captured }.isEqualTo("LOC1")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isJsonEqualTo(
                """
                {
                  "data": {
                        "country_code": "BE",
                        "party_id": "BEC",
                        "id": "LOC1",
                        "publish": true,
                        "name": "Gent Zuid",
                        "address": "F.Rooseveltlaan 3A",
                        "city": "Gent",
                        "postal_code": "9000",
                        "country": "BEL",
                        "coordinates": {
                            "latitude": "51.047599",
                            "longitude": "3.729944"
                        },
                        "parking_type": "ON_STREET",
                        "evses": [
                            {
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
                            {
                            "uid": "3257",
                            "evse_id": "BE*BEC*E041503002",
                            "status": "RESERVED",
                            "capabilities": [
                                "RESERVABLE"
                            ],
                            "connectors": [
                                {
                                "id": "1",
                                "standard": "IEC_62196_T2",
                                "format": "SOCKET",
                                "power_type": "AC_3_PHASE",
                                "max_voltage": 220,
                                "max_amperage": 16,
                                "tariff_ids": ["12"],
                                "last_updated": "2015-06-29T20:39:09Z"
                                }
                            ],
                            "physical_reference": "2",
                            "floor_level": "-2",
                            "last_updated": "2015-06-29T20:39:09Z"
                            }
                        ],
                        "operator": {
                            "name": "BeCharged"
                        },
                        "time_zone": "Europe/Brussels",
                        "last_updated": "2015-06-29T20:39:09Z"
                  },
                  "status_code": 1000,
                  "status_message": "Success",
                  "timestamp": "2015-06-30T21:59:59Z"
                }
                 """.trimIndent()
            )
        }
    }
}

private fun LocationsCpoRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)
    LocationsCpoServer(
        service = LocationsCpoService(this),
        basePath = "/locations"
    ).registerOn(transportServer)

    return transportServer.initRouterAndBuildClient()
}
