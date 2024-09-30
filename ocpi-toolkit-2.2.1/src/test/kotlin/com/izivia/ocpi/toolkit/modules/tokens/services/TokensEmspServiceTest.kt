package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.OcpiStatus
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.mock.getTokensEmspRepositoryTest
import com.izivia.ocpi.toolkit.modules.tokens.mock.postTokenEmspRepositoryTest
import com.izivia.ocpi.toolkit.modules.tokens.mock.validAuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.mock.validLocationReference
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.time.Instant

class TokensEmspServiceTest {

    private lateinit var service: TokensEmspService

    private val from = Instant.parse("2022-04-28T08:00:00.000Z")
    private val to = Instant.parse("2022-04-28T09:00:00.000Z")

    private val str1char = "a"
    private val str2chars = "ab"
    private val str3chars = "abc"
    private val str4chars = "abcd"
    private val str36chars = "abababababababababababababababababab"
    private val str37chars = "ababababababababababababababababababa"
    private val str40chars = "abababababababababababababababababababab"

    @Test
    fun getTokensParamsValidationTest() {
        service = TokensEmspService(service = getTokensEmspRepositoryTest(emptyList()))

        expectThat(runBlocking { service.getTokens(dateFrom = from, dateTo = from, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(runBlocking { service.getTokens(dateFrom = to, dateTo = from, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(runBlocking { service.getTokens(dateFrom = from, dateTo = to, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = to, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(runBlocking { service.getTokens(dateFrom = from, dateTo = null, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.SUCCESS.code)

            get { data }
                .isNotNull()
                .get(SearchResult<Token>::offset)
                .isEqualTo(0)
        }

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = -10, limit = null) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = -10) }) {
            get { statusCode }
                .isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)

            get { data }
                .isNull()
        }

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = 100) }) {
            get { statusCode }
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

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = 100, limit = 100) }) {
            get { statusCode }
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

        expectThat(runBlocking { service.getTokens(dateFrom = null, dateTo = null, offset = 0, limit = 0) }) {
            get { statusCode }
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
    fun postTokensParamsValidationTest() {
        service = TokensEmspService(service = postTokenEmspRepositoryTest(validAuthorizationInfo))

        expectThat(
            runBlocking {
                service.postToken(
                    tokenUid = str36chars,
                    type = TokenType.RFID,
                    locationReferences = validLocationReference,
                )
            },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking { service.postToken(tokenUid = str36chars, type = TokenType.RFID) },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking { service.postToken(tokenUid = str36chars, locationReferences = validLocationReference) },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking { service.postToken(tokenUid = str36chars) },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.SUCCESS.code)
        }

        expectThat(
            runBlocking { service.postToken(tokenUid = str40chars) },
        ) {
            get { statusCode }.isEqualTo(OcpiStatus.CLIENT_INVALID_PARAMETERS.code)
        }
    }
}
