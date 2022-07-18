package com.izivia.ocpi.toolkit.modules.tokens.validation

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.OcpiResponseBody.Companion.invalid
import com.izivia.ocpi.toolkit.common.OcpiResponseBody.Companion.notEnoughInformation
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
import com.izivia.ocpi.toolkit.modules.tokens.services.TokensEmspService
import java.time.Instant

class TokensEmspValidationService(
    private val service: TokensEmspService
) : TokensEmspInterface {

    override fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
        countryCode: String?,
        partyId: String?
    ): OcpiResponseBody<SearchResult<Token>> = OcpiResponseBody.of {
        validate {
            if (dateFrom != null && dateTo != null) validateDates("dateFrom", dateFrom, "dateTo", dateTo)
            if (limit != null) validateInt("limit", limit, 0, null)
            validateInt("offset", offset, 0, null)
            if (countryCode != null) validateLength("countryCode", countryCode, 2)
            if (partyId != null) validateLength("partyId", partyId, 3)
        }

        service
            .getTokens(dateFrom, dateTo, offset, limit, countryCode, partyId)
            .also { searchResult ->
                searchResult.list.forEach { token -> token.validate() }
            }
    }

    override fun getToken(tokenUid: String, tokenType: TokenType): OcpiResponseBody<AuthorizationInfo?> =
        OcpiResponseBody.of {
            validate {
                validateLength("tokenUid", tokenUid, 36)
            }

            service
                .getToken(tokenUid = tokenUid, tokenType = tokenType)
                ?.validate()
        }

    override fun postToken(
        tokenUid: String,
        tokenType: TokenType,
        locationReferences: LocationReferences
    ): OcpiResponseBody<AuthorizationInfo> =
        if (locationReferences.evse_uids.isEmpty()) {
            notEnoughInformation("Missing EVSE Id")
        } else if (locationReferences.evse_uids.size > 1) {
            invalid("Invalid or missing parameters: IOP does not support authorization request on multiple EVSE")
        } else {
            OcpiResponseBody.of {
                validate {
                    validateLength("tokenUid", tokenUid, 36)
                    locationReferences.validate()
                }

                service
                    .postToken(tokenUid = tokenUid, tokenType = tokenType, locationReferences = locationReferences)
                    .validate()
            }
        }
}
