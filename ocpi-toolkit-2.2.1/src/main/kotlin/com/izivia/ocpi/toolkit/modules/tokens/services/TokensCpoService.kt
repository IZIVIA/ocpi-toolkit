package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.domain.toPartial
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository

open class TokensCpoService(
    private val repository: TokensCpoRepository,
) : TokensCpoInterface {

    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): Token? {
        return repository.getToken(countryCode, partyId, tokenUid, type)
    }

    override suspend fun putToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: Token,
    ): TokenPartial {
        return repository.putToken(countryCode, partyId, tokenUid, type, token)
            .toPartial()
    }

    override suspend fun patchToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: TokenPartial,
    ): TokenPartial? {
        return repository.patchToken(countryCode, partyId, tokenUid, type, token)
            ?.toPartial()
    }
}
