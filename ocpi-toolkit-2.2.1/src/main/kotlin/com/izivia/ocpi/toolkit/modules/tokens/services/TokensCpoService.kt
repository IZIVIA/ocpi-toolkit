package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.tokens.repositories.TokensCpoRepository

class TokensCpoService(
    private val service: TokensCpoRepository
) : TokensCpoInterface {
    override fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        service.getToken(countryCode, partyId, tokenUid, type).validate()
    }

    override fun putToken(
        token: Token,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            token.validate()
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        service.putToken(token, countryCode, partyId, tokenUid, type).validate()
    }

    override fun patchToken(
        token: TokenPartial,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType?
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
