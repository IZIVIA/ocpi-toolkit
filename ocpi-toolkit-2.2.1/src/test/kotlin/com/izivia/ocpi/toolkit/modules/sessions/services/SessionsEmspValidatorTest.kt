package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.OcpiClientInvalidParametersException
import com.izivia.ocpi.toolkit.modules.sessions.SessionsEmspInterface
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectDoesNotThrow
import strikt.api.expectThrows
import java.math.BigDecimal
import java.time.Instant

class SessionsEmspValidatorTest {
    private val service = SessionsEmspValidator(
        mockk<SessionsEmspInterface> {
            coEvery { patchSession(any(), any(), any(), any()) } returns null
        },
    )

    @Test
    fun patchSessionWithLastUpdatedTest() {
        expectDoesNotThrow {
            runBlocking {
                service.patchSession(
                    countryCode = "FR",
                    partyId = "IZI",
                    sessionId = "session1",
                    session = SessionPartial(kwh = BigDecimal.ONE, lastUpdated = Instant.now()),
                )
            }
        }
    }

    @Test
    fun patchSessionWithoutLastUpdatedTest() {
        expectThrows<OcpiClientInvalidParametersException> {
            runBlocking {
                service.patchSession(
                    countryCode = "FR",
                    partyId = "IZI",
                    sessionId = "session1",
                    session = SessionPartial(kwh = BigDecimal.ONE),
                )
            }
        }
    }
}
