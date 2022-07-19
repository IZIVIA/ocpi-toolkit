package com.izivia.ocpi.toolkit.modules.tokens.services

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import java.time.Instant

/**
 * This interface enables the CPO to request the current list of Tokens, when needed. Via the POST method it is possible
 * to authorize a single token. Example endpoint structure: /ocpi/emsp/2.0/tokens/?date_from=xxx&date_to=yyy
 *
 * - GET: Get the list of known Tokens, last updated between the {date_from} and {date_to} (paginated)
 * - POST: Real-time authorization request
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface TokensEmspService {

    /**
     * Updated by Gireve from OCPI 2.1.1 (added countryCode / partyId)
     *
     * Fetch information about Tokens known in the eMSP systems.
     *
     * If additional parameters: {date_from} and/or {date_to} are provided, only Tokens with (last_updated) between the
     * iven date_from and date_to will be returned.
     *
     * This request is paginated, it supports the pagination related URL parameters.
     *
     * Gireve notes:
     * The standard OCPI 2.1.1 Tokens pulling allows CPOs to get Tokens of all eMSPs in contract with them. In some
     * cases, CPOs need only Tokens of a specific given eMSP. For example, when the CPO initializes data of an eMSP
     * after signature of a new roaming agreement. GIREVE provides a new OCPI 2.1.1 feature by allowing the CPO to get
     * Tokens of a unique eMSP by filling two dedicated OCPI headers in their “GET Tokens” request to GIREVE:
     * - ocpi-to-country-code: The country code of the targeted eMSP.
     * - ocpi-to-party-id: The party id of the targeted eMSP.
     * Therefore, CPOs can request GIREVE without these headers to get Tokens of all eMSPs or including these headers to
     * get Tokens of a unique eMSP. For information, these headers have been included in the version 2.2 of the OCPI
     * standard.
     *
     * @param dateFrom Only return Tokens that have last_updated after this Date/Time.
     * @param dateTo Only return Tokens that have last_updated before this Date/Time.
     * @param offset The offset of the first object returned. Default is 0.
     * @param limit Maximum number of objects to GET.
     * @param countryCode (max-length 2) The party id of the targeted eMSP.
     * @param partyId (max-length 3) Party ID (Provider ID) of the eMSP requesting this GET from the CPO system.
     * @return The endpoint response with list of valid Token objects, the header will contain the pagination related
     * headers.
     */
    fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
        countryCode: String?,
        partyId: String?
    ): SearchResult<Token>

    /**
     * Gireve-unique feature
     *
     * IOP adds a new OCPI feature enabling a CPO to retrieve the full description of a Token through the Tokens.uid.
     *
     * If the CPO is allowed to get Tokens of the eMSP owner, the response includes the full description of the Token.
     *
     * Gireve notes:
     * This new flow prevents CPOs to download all Tokens of all eMSPs. For more description, see 2.3.5 Custom OCPI flow
     * to prevent eMSP Tokens download by CPOs
     *
     * @param tokenUid (max-length 36) 	Token.uid of the Token for which this authorization is.
     * @param tokenType Token.type of the Token for which this authorization is. Default if omitted: RFID
     * @return Standard OCPI response including the Token description in data field. In case of an unknown Token or
     * Token not visible by the CPO, the status_code "2000" is returned
     */
    fun getToken(
        tokenUid: String,
        tokenType: TokenType = TokenType.RFID
    ): Token?

    /**
     * Do a 'real-time' authorization request to the eMSP system, validating if a Token might be used (at the optionally
     * given Location).
     *
     * Example endpoint structure: /ocpi/emsp/2.0/tokens/{token_uid}/authorize?{type=token_type} The /authorize is
     * required for the real-time authorize request.
     *
     * When the eMSP receives a 'real-time' authorization request from a CPO that contains too little information (no
     * LocationReferences provided) to determine if the Token might be used, the eMSP SHOULD respond with the OCPI
     * status: 2002
     *
     * In the body an optional LocationReferences object can be given. The eMSP SHALL then validate if the Token is
     * allowed to be used at this Location, and if applicable: which of the Locations EVSEs/Connectors. The object with
     * valid Location and EVSEs/Connectors will be returned in the response.
     *
     * @param tokenUid (max-length 36) 	Token.uid of the Token for which this authorization is.
     * @param tokenType Token.type of the Token for which this authorization is. Default if omitted: RFID
     * @param locationReferences Location and EVSEs/Connectors for which the Token is requested to be authorized.
     * @return Contains information about the authorization, if the Token is allowed to charge and optionally which
     * EVSEs/Connectors are allowed to be used.
     */
    fun postToken(
        tokenUid: String,
        tokenType: TokenType = TokenType.RFID,
        locationReferences: LocationReferences
    ): AuthorizationInfo
}
