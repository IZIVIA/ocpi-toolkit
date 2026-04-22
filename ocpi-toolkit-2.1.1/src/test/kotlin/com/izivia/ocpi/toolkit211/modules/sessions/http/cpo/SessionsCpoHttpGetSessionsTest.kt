package com.izivia.ocpi.toolkit211.modules.sessions.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.sessions.SessionsCpoInterface
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionStatusType
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

class SessionsCpoHttpGetSessionsTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should list sessions`(serializer: OcpiSerializer) {
        mapper = serializer
        val srv = mockk<SessionsCpoInterface> {
            coEvery { getSessions(any(), any(), any(), any()) } coAnswers {
                listOf(
                    Session(
                        id = "101",
                        startDateTime = Instant.parse("2015-06-29T22:39:09Z"),
                        endDateTime = Instant.parse("2015-06-29T23:50:16Z"),
                        kwh = BigDecimal("41.00"),
                        authId = "FA54320",
                        authMethod = AuthMethod.AUTH_REQUEST,
                        locationId = "LOC1",
                        evseUid = "3256",
                        connectorId = "1",
                        currency = "EUR",
                        totalCost = Price(BigDecimal("4.00")),
                        status = SessionStatusType.COMPLETED,
                        lastUpdated = Instant.parse("2015-06-29T23:50:17Z"),
                    ),
                ).toSearchResult()
            }
        }.buildServer()

        // when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/sessions/"),
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
      "id": "101",
      "start_date_time": "2015-06-29T22:39:09Z",
      "end_date_time": "2015-06-29T23:50:16Z",
      "kwh": 41.00,
      "auth_id": "FA54320",
      "auth_method": "AUTH_REQUEST",
      "location_id": "LOC1",
      "evse_uid": "3256",
      "connector_id": "1",
      "currency": "EUR",
      "total_cost": {"excl_vat": 4.00},
      "status": "COMPLETED",
      "last_updated": "2015-06-29T23:50:17Z"
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
