package com.izivia.ocpi.toolkit.modules.session

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.buildHttpRequest
import com.izivia.ocpi.toolkit.modules.cdr.domain.AuthMethod
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrToken
import com.izivia.ocpi.toolkit.modules.sessions.SessionsCpoServer
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionStatusType
import com.izivia.ocpi.toolkit.modules.sessions.repositories.SessionsCpoRepository
import com.izivia.ocpi.toolkit.modules.sessions.services.SessionsCpoService
import com.izivia.ocpi.toolkit.modules.toSearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
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
import java.math.BigDecimal
import java.time.Instant

class SessionsCpoHttpGetSessionsTest {
    @Test
    fun `shoud list sessions`() {
        //given
        val slots = object {
            var dateFrom = slot<Instant>()
            var dateTo = slot<Instant>()
        }
        val srv = mockk<SessionsCpoRepository>() {
            every { getSessions(capture(slots.dateFrom), capture(slots.dateTo), any(), any()) }
            listOf(
                Session(
                    country_code = "NL",
                    party_id = "STK",
                    id = "101",
                    start_date_time = Instant.parse("2020-03-09T10:17:09Z"),
                    kwh = BigDecimal.ZERO,
                    cdr_token = CdrToken(
                        uid = "123abc",
                        type = TokenType.RFID,
                        contract_id = "NL-TST-C12345678-S"
                    ),
                    auth_method = AuthMethod.WHITELIST,
                    location_id = "LOC1",
                    evse_uid = "3256",
                    connector_id = "1",
                    currency = "",
                    status = SessionStatusType.PENDING,
                    last_updated = Instant.parse("2019-07-01T12:12:11Z")
                )
            ).toSearchResult()
        }.buildServer()
        OcpiResponseBody.now = { Instant.parse("2015-06-30T21:59:59Z") }

        //when
        val resp: HttpResponse = srv.send(
            buildHttpRequest(HttpMethod.GET, "/sessions/?date_from=2019-01-28T12:00:00Z&date_to=2019-01-29T12:00:00Z")
        )

        //then
        expectThat(slots) {
            get { dateFrom.captured }.isEqualTo(Instant.parse("2019-01-28T12:00:00Z"))
            get { dateTo.captured }.isEqualTo(Instant.parse("2019-01-29T12:00:00Z"))
        }
        expectThat(resp) {
            get { status }.isEqualTo(HttpStatus.OK)
        }
    }
}

private fun SessionsCpoRepository.buildServer(): TransportClient {
    val transportServer = Http4kTransportServer("http://localhost:1234", 1234)
    SessionsCpoServer(
        service = SessionsCpoService(this),
        basePath = "/sessions"
    ).registerOn(transportServer)

    return transportServer.initRouterAndBuildClient()
}
