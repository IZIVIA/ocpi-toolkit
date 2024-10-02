package com.izivia.ocpi.toolkit.modules.locations.http.cpo

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.LocationsCpoServer
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.locations.repositories.LocationsCpoRepository
import com.izivia.ocpi.toolkit.modules.locations.services.LocationsCpoService
import com.izivia.ocpi.toolkit.modules.toSearchResult
import com.izivia.ocpi.toolkit.modules.versions.repositories.InMemoryVersionsRepository
import com.izivia.ocpi.toolkit.samples.common.Http4kTransportServer
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class LocationsCpoHttpGetLocationsTest {
    @Test
    fun `should list locations`() {
        // given
        val slots = object {
            var dateFrom = slot<Instant>()
            var dateTo = slot<Instant>()
        }
        val srv = mockk<LocationsCpoRepository> {
            coEvery { getLocations(capture(slots.dateFrom), capture(slots.dateTo), any(), any()) } coAnswers {
                listOf(
                    Location(
                        countryCode = "NL",
                        partyId = "ALF",
                        id = "3e7b39c2-10d0-4138-a8b3-8509a25f9920",
                        name = "ihomer",
                        address = "Tamboerijn 7",
                        city = "Etten-Leur",
                        postalCode = "4876 BS",
                        country = "NLD",
                        coordinates = GeoLocation("51.562787", "4.638975"),
                        parkingType = ParkingType.PARKING_LOT,
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
                                parkingRestrictions = listOf(ParkingRestriction.CUSTOMERS),
                                lastUpdated = Instant.parse("2019-07-01T12:12:11Z"),
                            ),
                        ),
                        publish = true,
                        timeZone = "Europe/Amsterdam",
                        lastUpdated = Instant.parse("2019-07-01T12:12:11Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/locations/?date_from=2019-01-28T12:00:00Z&date_to=2019-01-29T12:00:00Z"),
        )

        // then
        expectThat(slots) {
            get { dateFrom.captured }.isEqualTo(Instant.parse("2019-01-28T12:00:00Z"))
            get { dateTo.captured }.isEqualTo(Instant.parse("2019-01-29T12:00:00Z"))
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers["X-Total-Count"] }.isEqualTo("1")
            get { headers["X-Limit"] }.isEqualTo("50")
            get { body }.isJsonEqualTo(
                """
{
  "data": [
    {
      "country_code": "NL",
      "party_id": "ALF",
      "id": "3e7b39c2-10d0-4138-a8b3-8509a25f9920",
      "publish": true,
      "name": "ihomer",
      "address": "Tamboerijn 7",
      "city": "Etten-Leur",
      "postal_code": "4876 BS",
      "country": "NLD",
      "coordinates": {
        "latitude": "51.562787",
        "longitude": "4.638975"
      },
      "parking_type": "PARKING_LOT",
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
          "parking_restrictions": [
            "CUSTOMERS"
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
  "timestamp": "2015-06-30T21:59:59Z"
}
                """.trimIndent(),
            )
        }
    }
}

private fun LocationsCpoRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)

    val repo = this
    runBlocking {
        LocationsCpoServer(
            service = LocationsCpoService(repo),
            versionsRepository = InMemoryVersionsRepository(),
            basePathOverride = "/locations",
        ).registerOn(transportServer)
    }

    return transportServer.initRouterAndBuildClient()
}
