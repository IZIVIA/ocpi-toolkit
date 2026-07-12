package com.izivia.ocpi.toolkit211.modules.sessions.http.cpo

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.buildHttpRequest
import com.izivia.ocpi.toolkit211.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit211.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit211.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit211.modules.locations.domain.Location
import com.izivia.ocpi.toolkit211.modules.locations.domain.LocationType
import com.izivia.ocpi.toolkit211.modules.sessions.SessionsCpoInterface
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionStatusType
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
                        startDatetime = Instant.parse("2015-06-29T22:39:09Z"),
                        endDatetime = Instant.parse("2015-06-29T23:50:16Z"),
                        kwh = BigDecimal("41.00"),
                        authId = "FA54320",
                        authMethod = AuthMethod.AUTH_REQUEST,
                        location = Location(
                            id = "LOC1",
                            type = LocationType.ON_STREET,
                            address = "F.Rooseveltlaan 3A",
                            city = "Gent",
                            postalCode = "9000",
                            country = "BEL",
                            coordinates = GeoLocation("51.047599", "3.729944"),
                            lastUpdated = Instant.parse("2015-06-29T22:39:09Z"),
                        ),
                        currency = "EUR",
                        totalCost = BigDecimal("4.00"),
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
      "start_datetime": "2015-06-29T22:39:09Z",
      "end_datetime": "2015-06-29T23:50:16Z",
      "kwh": 41.00,
      "auth_id": "FA54320",
      "auth_method": "AUTH_REQUEST",
      "location": {
        "id": "LOC1",
        "type": "ON_STREET",
        "address": "F.Rooseveltlaan 3A",
        "city": "Gent",
        "postal_code": "9000",
        "country": "BEL",
        "coordinates": {"latitude": "51.047599", "longitude": "3.729944"},
        "last_updated": "2015-06-29T22:39:09Z"
      },
      "currency": "EUR",
      "total_cost": 4.00,
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
