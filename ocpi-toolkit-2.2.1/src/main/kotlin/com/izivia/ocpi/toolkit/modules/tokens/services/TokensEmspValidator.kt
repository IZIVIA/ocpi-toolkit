package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateDates
import com.izivia.ocpi.toolkit.common.validation.validateInt
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.tokens.TokensEmspInterface
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import java.time.Instant

open class TokensEmspValidator(
    private val service: TokensEmspInterface,
) : TokensEmspInterface {

    override suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Token> {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
        }

        return service.getTokens(dateFrom, dateTo, offset, limit)
            .also { searchResult ->
                searchResult.list.forEach { token -> token.validate() }
            }
    }

    override suspend fun postToken(
        tokenUid: CiString,
        type: TokenType?,
        locationReferences: LocationReferences?,
    ): AuthorizationInfo {
        validate {
            validateLength("tokenUid", tokenUid, 36)
            locationReferences?.validate()
        }

        return service.postToken(tokenUid, type, locationReferences).validate()
    }
}
