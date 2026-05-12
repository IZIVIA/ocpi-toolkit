package com.izivia.ocpi.toolkit211.modules.tariff.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.tariff.TariffCpoInterface
import com.izivia.ocpi.toolkit211.modules.tariff.domain.PriceComponent
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.tariff.domain.TariffDimensionType
import com.izivia.ocpi.toolkit211.modules.tariff.domain.TariffElement
import com.izivia.ocpi.toolkit211.modules.toSearchResult
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

class TariffCpoHttpGetTariffsTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should list tariffs`(serializer: OcpiSerializer) {
        mapper = serializer
        val srv = mockk<TariffCpoInterface> {
            coEvery { getTariffs(any(), any(), any(), any()) } coAnswers {
                listOf(
                    Tariff(
                        id = "12",
                        currency = "EUR",
                        elements = listOf(
                            TariffElement(
                                priceComponents = listOf(
                                    PriceComponent(
                                        type = TariffDimensionType.TIME,
                                        price = BigDecimal("2.00"),
                                        stepSize = 300,
                                    ),
                                ),
                            ),
                        ),
                        lastUpdated = Instant.parse("2015-06-29T22:39:09Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/tariffs/"),
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
      "id": "12",
      "currency": "EUR",
      "elements": [
        {
          "price_components": [
            {
              "type": "TIME",
              "price": 2.00,
              "step_size": 300
            }
          ]
        }
      ],
      "last_updated": "2015-06-29T22:39:09Z"
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
