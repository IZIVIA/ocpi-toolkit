package com.izivia.ocpi.toolkit211.modules.locations.http.emsp

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.locations.repositories.LocationsEmspRepository
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.mapper
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.time.Instant

class LocationsEmspHttpGetLocationTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should get location`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var countryCode = slot<String>()
            var partyId = slot<String>()
            var locationId = slot<String>()
        }
        val srv = mockk<LocationsEmspRepository> {
            coEvery {
                getLocation(
                    capture(slots.countryCode),
                    capture(slots.partyId),
                    capture(slots.locationId),
                )
            } coAnswers {
                Location(
                    id = "LOC1",
                    name = "Gent Zuid",
                    address = "F.Rooseveltlaan 3A",
                    city = "Gent",
                    postalCode = "9000",
                    country = "BEL",
                    coordinates = GeoLocation("51.047599", "3.729944"),
                    evses = listOf(
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
                                    tariffId = "11",
                                    lastUpdated = Instant.parse("2015-03-16T10:10:02Z"),
                                ),
                            ),
                            floorLevel = "-1",
                            physicalReference = "1",
                            lastUpdated = Instant.parse("2015-06-28T08:12:01Z"),
                        ),
                    ),
                    operator = BusinessDetails(name = "BeCharged"),
                    timeZone = "Europe/Brussels",
                    lastUpdated = Instant.parse("2015-06-29T20:39:09Z"),
                )
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/BE/BEC/LOC1"),
        )

        // then
        expectThat(slots) {
            get { countryCode.captured }.isEqualTo("BE")
            get { partyId.captured }.isEqualTo("BEC")
            get { locationId.captured }.isEqualTo("LOC1")
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { body }.isNotNull().isJsonEqualTo(
                """
                {
                  "data": {
                    "id": "LOC1",
                    "name": "Gent Zuid",
                    "address": "F.Rooseveltlaan 3A",
                    "city": "Gent",
                    "postal_code": "9000",
                    "country": "BEL",
                    "coordinates": {
                      "latitude": "51.047599",
                      "longitude": "3.729944"
                    },
                    "evses": [
                      {
                        "uid": "3256",
                        "evse_id": "BE*BEC*E041503001",
                        "status": "AVAILABLE",
                        "capabilities": ["RESERVABLE"],
                        "connectors": [
                          {
                            "id": "1",
                            "standard": "IEC_62196_T2",
                            "format": "CABLE",
                            "power_type": "AC_3_PHASE",
                            "max_voltage": 220,
                            "max_amperage": 16,
                            "tariff_id": "11",
                            "last_updated": "2015-03-16T10:10:02Z"
                          }
                        ],
                        "physical_reference": "1",
                        "floor_level": "-1",
                        "last_updated": "2015-06-28T08:12:01Z"
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
                """.trimIndent(),
            )
        }
    }
}
