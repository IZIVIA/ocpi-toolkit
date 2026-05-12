package com.izivia.ocpi.toolkit211.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.locations.LocationsCpoInterface
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.toSearchResult
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

class LocationsCpoHttpGetLocationsTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should list locations`(serializer: OcpiSerializer) {
        mapper = serializer
        val slots = object {
            var dateFrom = slot<Instant?>()
            var dateTo = slot<Instant?>()
        }
        val srv = mockk<LocationsCpoInterface> {
            coEvery {
                getLocations(captureNullable(slots.dateFrom), captureNullable(slots.dateTo), any(), any())
            } coAnswers {
                listOf(
                    Location(
                        id = "3e7b39c2-10d0-4138-a8b3-8509a25f9920",
                        name = "ihomer",
                        address = "Tamboerijn 7",
                        city = "Etten-Leur",
                        postalCode = "4876 BS",
                        country = "NLD",
                        coordinates = GeoLocation("51.562787", "4.638975"),
                        evses = listOf(
                            Evse(
                                uid = "fd855359-bc81-47bb-bb89-849ae3dac89e",
                                evseId = "NL*ALF*E000000001",
                                status = Status.AVAILABLE,
                                connectors = listOf(
                                    Connector(
                                        id = "1",
                                        standard = ConnectorType.IEC_62196_T2,
                                        format = ConnectorFormat.SOCKET,
                                        powerType = PowerType.AC_3_PHASE,
                                        maxVoltage = 220,
                                        maxAmperage = 16,
                                        lastUpdated = Instant.parse("2019-07-01T12:12:11Z"),
                                    ),
                                ),
                                lastUpdated = Instant.parse("2019-07-01T12:12:11Z"),
                            ),
                        ),
                        timeZone = "Europe/Amsterdam",
                        lastUpdated = Instant.parse("2019-07-01T12:12:11Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/?date_from=2019-01-28T12:00:00Z&date_to=2019-01-29T12:00:00Z"),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers["X-Total-Count"] }.isEqualTo("1")
            get { headers["X-Limit"] }.isEqualTo("50")
            get { body }.isNotNull().isJsonEqualTo(
                """
{
  "data": [
    {
      "id": "3e7b39c2-10d0-4138-a8b3-8509a25f9920",
      "name": "ihomer",
      "address": "Tamboerijn 7",
      "city": "Etten-Leur",
      "postal_code": "4876 BS",
      "country": "NLD",
      "coordinates": {
        "latitude": "51.562787",
        "longitude": "4.638975"
      },
      "evses": [
        {
          "uid": "fd855359-bc81-47bb-bb89-849ae3dac89e",
          "evse_id": "NL*ALF*E000000001",
          "status": "AVAILABLE",
          "connectors": [
            {
              "id": "1",
              "standard": "IEC_62196_T2",
              "format": "SOCKET",
              "power_type": "AC_3_PHASE",
              "max_voltage": 220,
              "max_amperage": 16,
              "last_updated": "2019-07-01T12:12:11Z"
            }
          ],
          "last_updated": "2019-07-01T12:12:11Z"
        }
      ],
      "time_zone": "Europe/Amsterdam",
      "last_updated": "2019-07-01T12:12:11Z"
    }
  ],
  "status_code": 1000,
  "status_message": "Success",
  "timestamp": "$nowString"
}
                """.trimIndent(),
            )
        }
    }
}
