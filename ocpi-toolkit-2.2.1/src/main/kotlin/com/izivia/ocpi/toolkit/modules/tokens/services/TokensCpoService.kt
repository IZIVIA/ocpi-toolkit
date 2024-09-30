package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateSame
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository

open class TokensCpoService(
    private val service: TokensCpoRepository,
) : TokensCpoInterface {
    override suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        service.getToken(countryCode, partyId, tokenUid, type).validate()
    }

    override suspend fun putToken(
        token: Token,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            token.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
            validateSame("countryCode", countryCode, token.countryCode)
            validateSame("partyId", partyId, token.partyId)
            validateSame("tokenUid", tokenUid, token.uid)
        }

        service.putToken(token, countryCode, partyId, tokenUid, type).validate()
    }

    override suspend fun patchToken(
        token: TokenPartial,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?,
    ): OcpiResponseBody<Token?> = OcpiResponseBody.of {
        validate {
            token.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        service.patchToken(token, countryCode, partyId, tokenUid, type)?.validate()
    }
}
