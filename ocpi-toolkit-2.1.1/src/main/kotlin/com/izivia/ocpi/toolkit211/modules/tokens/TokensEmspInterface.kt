package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.SearchResult
import com.izivia.ocpi.toolkit211.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit211.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import java.time.Instant

interface TokensEmspInterface {

    suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): SearchResult<Token>

    suspend fun postToken(
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
        locationReferences: LocationReferences? = null,
    ): AuthorizationInfo
}
