package com.izivia.ocpi.toolkit.modules.tokens.mock

import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensEmspRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant

fun getTokensEmspRepositoryTest(tokens: List<Token>): TokensEmspRepository = mockk {
    val dateFrom = mutableListOf<Instant?>()
    val dateTo = mutableListOf<Instant?>()
    val offset = slot<Int>()
    val limit = mutableListOf<Int?>()

    coEvery {
        getTokens(
            captureNullable(dateFrom),
            captureNullable(dateTo),
            capture(offset),
            captureNullable(limit),
        )
    } coAnswers {
        val capturedOffset = offset.captured
        val capturedLimit = limit.captured() ?: 10

        tokens
            .filter { token ->
                val dateFromFilterOk = dateFrom.captured()?.let { token.lastUpdated.isAfter(it) } ?: true
                val dateToFilterOk = dateTo.captured()?.let { token.lastUpdated.isBefore(it) } ?: true

                dateFromFilterOk && dateToFilterOk
            }
            .filterIndexed { index: Int, _: Token ->
                index >= capturedOffset
            }
            .take(capturedLimit)
            .toSearchResult(totalCount = tokens.size, limit = capturedLimit, offset = capturedOffset)
    }
}

fun postTokenEmspRepositoryTest(authorizationInfo: AuthorizationInfo): TokensEmspRepository = mockk {
    val tokenUid = slot<String>()
    val type = mutableListOf<TokenType?>()
    val locationReferences = mutableListOf<LocationReferences?>()

    coEvery {
        postToken(capture(tokenUid), captureNullable(type), captureNullable(locationReferences))
    } coAnswers { authorizationInfo }
}
