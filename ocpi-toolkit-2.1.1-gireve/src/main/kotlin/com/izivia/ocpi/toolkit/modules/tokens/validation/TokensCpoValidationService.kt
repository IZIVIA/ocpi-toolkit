package com.izivia.ocpi.toolkit.modules.tokens.validation

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.modules.tokens.TokensCpoInterface
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensCpoService

class TokensCpoValidationService(
    private val service: TokensCpoService
): TokensCpoInterface {

    override fun getToken(
        countryCode: String,
        partyId: String,
        tokenUid: String
    ): OcpiResponseBody<Token?> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
        }

        service
            .getToken(countryCode = countryCode, partyId = partyId, tokenUid = tokenUid)
            ?.validate()
    }

    override fun putToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: Token
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
            token.validate()
        }

        service
            .putToken(countryCode = countryCode, partyId = partyId, tokenUid = tokenUid, token = token)
            .validate()
    }

    override fun patchToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: TokenPartial
    ): OcpiResponseBody<Token> = OcpiResponseBody.of {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tokenUid", tokenUid, 36)
            token.validate()
        }

        service
            .patchToken(countryCode = countryCode, partyId = partyId, tokenUid = tokenUid, token = token)
            .validate()
    }

}
