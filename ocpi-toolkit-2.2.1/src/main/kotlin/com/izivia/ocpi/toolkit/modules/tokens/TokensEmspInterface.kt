package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.modules.tokens.domain.AuthorizationInfo
import com.izivia.ocpi.toolkit.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import java.time.Instant

/**
 * Typically implemented by market roles like: eMSP.
 *
 * This interface enables the Receiver to request the current list of Tokens, when needed. Via the POST method it is possible to
 * authorize a single token.
 *
 * - GET: Get the list of known Tokens, last updated between the {date_from} and {date_to} (paginated)
 * - POST: Real-time authorization request
 * - PUT: n/a
 * - PATCH: n/a
 * - DELETE: n/a
 */
interface TokensEmspInterface {
    /**
     * GET Method
     *
     * Fetch information about Tokens known in the eMSP systems.
     *
     * If additional parameters: {date_from} and/or {date_to} are provided, only Tokens with (last_updated) between the given
     * {date_from} (including) and {date_to} (excluding) will be returned.
     *
     * This request is paginated, it supports the pagination related URL parameters. This request is paginated, it supports the pagination
     * related URL parameters.
     *
     * @param dateFrom Instant? Only return Tokens that have last_updated after this Date/Time.
     * @param dateTo Instant? Only return Tokens that have last_updated before this Date/Time.
     * @param offset Int? The offset of the first object returned. Default is 0.
     * @param limit Int? Maximum number of objects to GET.
     * @return List<Token> The endpoint response with list of valid Token objects, the header will contain the pagination related headers.
     * Any older information that is not specified in the response is considered as no longer valid. Each object must contain all required
     * fields. Fields that are not specified may be considered as null values.
     */
    suspend fun getTokens(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int = 0,
        limit: Int?,
    ): OcpiResponseBody<SearchResult<Token>>

    /**
     * POST Method
     *
     * Do a 'real-time' authorization request to the eMSP system, validating if a Token might be used (at the optionally given Location).
     *
     * When the eMSP does not know the Token, the eMSP SHALL respond with an HTTP status code: 404 (Not Found).
     *
     * When the eMSP receives a 'real-time' authorization request from a CPO that contains too little information (no LocationReferences
     * provided) to determine if the Token might be used, the eMSP SHALL respond with the OCPI status: 2002
     *
     * The parameter: token_uid has to be provided as URL segments.
     * The parameter: type may be provided as an URL parameter
     *
     * In the body an optional LocationReferences object can be given. The eMSP SHALL then validate if the Token is allowed to be used
     * at this Location, and if applicable: which of the Locations EVSEs. The object with valid Location and EVSEs will be returned in the
     * response.
     *
     * When the token is known by the Sender, the response SHALL contain a AuthorizationInfo object.
     * If the token is not known, the response SHALL contain the status code: 2004: Unknown Token, and no data field.
     *
     * @param tokenUid Token.uid of the Token for which authorization is requested. max-length = 36.
     * @param type Token.type of the Token for which this authorization is. Default if omitted: RFID
     * @param locationReferences Location and EVSEs for which the Token is requested to be authorized.
     * @return AuthorizationInfo Contains information about the authorization, if the Token is allowed to charge and
     * optionally which EVSEs are allowed to be used.
     */

    suspend fun postToken(
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
        locationReferences: LocationReferences? = null,
    ): OcpiResponseBody<AuthorizationInfo>
}
