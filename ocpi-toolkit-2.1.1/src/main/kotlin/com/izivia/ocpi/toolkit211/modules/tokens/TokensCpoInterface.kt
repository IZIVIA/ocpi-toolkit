package com.izivia.ocpi.toolkit211.modules.tokens

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType

interface TokensCpoInterface {

    suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
    ): Token?

    suspend fun putToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
        token: Token,
    ): TokenPartial

    suspend fun patchToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
        token: TokenPartial,
    ): TokenPartial?
}
