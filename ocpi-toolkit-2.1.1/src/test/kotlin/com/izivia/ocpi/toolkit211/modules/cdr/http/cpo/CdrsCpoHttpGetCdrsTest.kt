package com.izivia.ocpi.toolkit211.modules.cdr.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.cdr.CdrsCpoInterface
import com.izivia.ocpi.toolkit211.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.cdr.domain.CdrDimension
import com.izivia.ocpi.toolkit211.modules.cdr.domain.CdrDimensionType
import com.izivia.ocpi.toolkit211.modules.cdr.domain.ChargingPeriod
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.toSearchResult
import com.izivia.ocpi.toolkit211.modules.types.Price
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.mapper
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import java.math.BigDecimal
import java.time.Instant

class CdrsCpoHttpGetCdrsTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should list cdrs`(serializer: OcpiSerializer) {
        mapper = serializer
        val srv = mockk<CdrsCpoInterface> {
            coEvery { getCdrs(any(), any(), any(), any()) } coAnswers {
                listOf(
                    Cdr(
                        id = "CDR0001",
                        startDateTime = Instant.parse("2015-06-29T21:39:09Z"),
                        endDateTime = Instant.parse("2015-06-29T23:50:16Z"),
                        authId = "FA54320",
                        authMethod = AuthMethod.AUTH_REQUEST,
                        locationId = "LOC1",
                        evseId = "BE*BEC*E041503001",
                        connectorId = "1",
                        currency = "EUR",
                        tariffs = null,
                        chargingPeriods = listOf(
                            ChargingPeriod(
                                startDateTime = Instant.parse("2015-06-29T21:39:09Z"),
                                dimensions = listOf(
                                    CdrDimension(
                                        type = CdrDimensionType.ENERGY,
                                        volume = BigDecimal("120.00"),
                                    ),
                                ),
                            ),
                        ),
                        totalCost = Price(BigDecimal("4.00")),
                        totalEnergy = BigDecimal("41.00"),
                        totalTime = BigDecimal("0.973"),
                        lastUpdated = Instant.parse("2015-06-29T22:01:13Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/cdrs/"),
        )

        // then
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
            get { headers["X-Total-Count"] }.isEqualTo("1")
            get { body }.isNotNull().isJsonEqualTo(
                """
{
  "data": [
    {
      "id": "CDR0001",
      "start_date_time": "2015-06-29T21:39:09Z",
      "end_date_time": "2015-06-29T23:50:16Z",
      "auth_id": "FA54320",
      "auth_method": "AUTH_REQUEST",
      "location_id": "LOC1",
      "evse_id": "BE*BEC*E041503001",
      "connector_id": "1",
      "currency": "EUR",
      "charging_periods": [
        {
          "start_date_time": "2015-06-29T21:39:09Z",
          "dimensions": [
            {
              "type": "ENERGY",
              "volume": 120.00
            }
          ]
        }
      ],
      "total_cost": {"excl_vat": 4.00},
      "total_energy": 41.00,
      "total_time": 0.973,
      "last_updated": "2015-06-29T22:01:13Z"
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
