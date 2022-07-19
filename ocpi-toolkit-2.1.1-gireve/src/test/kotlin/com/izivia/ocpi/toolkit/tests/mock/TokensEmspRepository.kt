package com.izivia.ocpi.toolkit.tests.mock

import com.izivia.ocpi.toolkit.common.toSearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensEmspService
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant

fun tokensEmspService(tokens: List<Token>): TokensEmspService = mockk {
    val dateFrom = mutableListOf<Instant?>()
    val dateTo = mutableListOf<Instant?>()
    val offset = slot<Int>()
    val limit = mutableListOf<Int?>()
    val countryCode = mutableListOf<String?>()
    val partyId = mutableListOf<String?>()

    val tokenUid = slot<String>()
    val tokenType = slot<TokenType>()
    val locationReferences = slot<LocationReferences>()

    every {
        getTokens(
            captureNullable(dateFrom),
            captureNullable(dateTo),
            capture(offset),
            captureNullable(limit),
            captureNullable(countryCode),
            captureNullable(partyId),
        )
    } answers {
        val capturedOffset = offset.captured
        val capturedLimit = limit.captured() ?: 10

        tokens
            .filter { token ->
                val dateFromFilterOk = dateFrom.captured()?.let { token.last_updated.isAfter(it) } ?: true
                val dateToFilterOk = dateTo.captured()?.let { token.last_updated.isBefore(it) } ?: true

                dateFromFilterOk && dateToFilterOk
            }
            .filterIndexed { index: Int, _: Token ->
                index >= capturedOffset
            }
            .take(capturedLimit)
            .toSearchResult(totalCount = tokens.size, limit = capturedLimit, offset = capturedOffset)
    }

    every { getToken(capture(tokenUid), capture(tokenType)) } answers {
        tokens.find { it.uid == tokenUid.captured }
    }

    every { postToken(capture(tokenUid), capture(tokenType), capture(locationReferences)) } answers {
        tokens
            .find { it.uid == tokenUid.captured }
            .let {
                AuthorizationInfo(
                    allowed = Allowed.ALLOWED,
                    location = locationReferences.captured,
                    info = null,
                    authorization_id = "auth_id"
                )
            }
    }
}
