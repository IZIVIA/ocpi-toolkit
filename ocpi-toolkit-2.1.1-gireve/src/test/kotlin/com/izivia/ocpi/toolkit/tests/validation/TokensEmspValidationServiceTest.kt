package com.izivia.ocpi.toolkit.tests.validation

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.validation.TokensEmspValidationService
import com.izivia.ocpi.toolkit.samples.common.validEvse
import com.izivia.ocpi.toolkit.samples.common.validLocation
import com.izivia.ocpi.toolkit.samples.common.validLocationReferences
import com.izivia.ocpi.toolkit.samples.common.validToken
import com.izivia.ocpi.toolkit.tests.mock.tokensEmspService
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.time.Instant
import java.util.*

class TokensEmspValidationServiceTest {
    private lateinit var service: TokensEmspValidationService
    private val from = Instant.parse("2022-04-28T08:00:00.000Z")
    private val to = Instant.parse("2022-04-28T09:00:00.000Z")
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getTokensParamsValidationTest() {
        service = TokensEmspValidationService(service = tokensEmspService(emptyList()))

        expectThat(service.getTokens(dateFrom = from, dateTo = from, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getTokens(dateFrom = to, dateTo = from, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(service.getTokens(dateFrom = from, dateTo = to, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = to, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getTokens(dateFrom = from, dateTo = null, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = -10, limit = null)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = -10)) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = 100)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::limit)
                .isEqualTo(100)
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = 100, limit = 100)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(100)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::limit)
                .isEqualTo(100)
        }

        expectThat(service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = 0)) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::limit)
                .isEqualTo(0)
        }
    }

    @Test
    fun postTokenParamsValidationTest() {
        service = TokensEmspValidationService(service = tokensEmspService(emptyList()))

        expectThat(
            service.postToken(
                tokenUid = validToken.uid,
                tokenType = TokenType.RFID,
                locationReferences = null
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.postToken(
                tokenUid = validToken.uid,
                tokenType = TokenType.RFID,
                locationReferences = validLocationReferences
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            service.postToken(
                tokenUid = str37chars,
                tokenType = TokenType.RFID,
                locationReferences = null
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.postToken(
                tokenUid = validToken.uid,
                tokenType = TokenType.RFID,
                locationReferences = LocationReferences(
                    location_id = str40chars,
                    evse_uids = emptyList(),
                    connector_ids = emptyList()
                )
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.postToken(
                tokenUid = validToken.uid,
                tokenType = TokenType.RFID,
                locationReferences = LocationReferences(
                    location_id = validLocation.id,
                    evse_uids = listOf(str40chars),
                    connector_ids = emptyList()
                )
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }

        expectThat(
            service.postToken(
                tokenUid = validToken.uid,
                tokenType = TokenType.RFID,
                locationReferences = LocationReferences(
                    location_id = validLocation.id,
                    evse_uids = listOf(validEvse.uid),
                    connector_ids = listOf(str40chars)
                )
            )
        ) {
            get { status_code }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
