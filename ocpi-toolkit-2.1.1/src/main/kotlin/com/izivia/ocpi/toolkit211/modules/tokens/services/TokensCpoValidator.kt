package com.izivia.ocpi.toolkit211.modules.tokens.services

import com.izivia.ocpi.toolkit211.common.CiString
import com.izivia.ocpi.toolkit211.common.validation.validate
import com.izivia.ocpi.toolkit211.common.validation.validateLength
import com.izivia.ocpi.toolkit211.common.validation.validateSame
import com.izivia.ocpi.toolkit211.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType

open class TokensCpoValidator(
    private val service: TokensCpoInterface,
) : TokensCpoInterface {

    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): Token? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        return service.getToken(countryCode, partyId, tokenUid, type)?.validate()
    }

    override suspend fun putToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: Token,
    ): TokenPartial {
        validate {
            token.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
            validateSame("tokenUid", tokenUid, token.uid)
        }

        return service.putToken(countryCode, partyId, tokenUid, type, token).validate()
    }

    override suspend fun patchToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
        token: TokenPartial,
    ): TokenPartial? {
        validate {
            token.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        return service.patchToken(countryCode, partyId, tokenUid, type, token)?.validate()
    }
}
