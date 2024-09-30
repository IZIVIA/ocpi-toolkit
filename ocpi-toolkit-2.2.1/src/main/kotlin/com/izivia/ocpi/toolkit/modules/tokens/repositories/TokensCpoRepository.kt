package com.izivia.ocpi.toolkit.modules.tokens.repositories

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType

/**
 * Typically implemented by market roles like: CPO.
 *
 * With this interface the Sender can push the Token information to the Receiver. Tokens is a Client Owned Object, so the end-points
 * need to contain the required extra fields: {party_id} and {country_code}.
 *
 * - GET: Retrieve a Token as it is stored in the CPO system.
 * - POST: n/a
 * - PUT: Push new/updated Token object to the CPO.
 * - PATCH: Notify the CPO of partial updates to a Token.
 * - DELETE: n/a, (Use PUT, Tokens cannot be removed).
 */
interface TokensCpoRepository {
    /**
     * GET Method
     *
     * If the eMSP wants to check the status of a Token in the CPO system it might GET the object from the CPO system for validation
     * purposes. The eMSP is the owner of the objects, so it would be illogical if the CPO system had a different status or was missing an
     * object.
     * The following parameters: country_code, party_id, token_uid have to be provided as URL segments.
     * The parameter: type may be provided as an URL parameter
     *
     * @param countryCode Country code of the eMSP requesting this GET from the CPO system. max-length = 36
     * @param partyId Party ID (Provider ID) of the eMSP requesting this GET from the CPO system. max-length = 36
     * @param tokenUid Token.uid of the Token object to retrieve. max-length = 36
     * @param type Token.type of the Token to retrieve. Default if omitted: RFID
     * @return The requested Token object.
     */
    suspend fun getToken(
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
    ): Token

    /**
     * PUT Method
     *
     * New or updated Token objects are pushed from the eMSP to the CPO
     *
     * @param token (Request Body) New or updated Token object
     * @param countryCode (Request parameter) Country code of the eMSP sending this PUT request to the CPO system. This
     * SHALL be the same value as the country_code in the Token object being
     * pushed. max-length = 36
     * @param partyId (Request parameter) Party ID (Provider ID) of the eMSP sending this PUT request to the CPO
     * system. This SHALL be the same value as the party_id in the Token object
     * being pushed. max-length = 36
     * @param tokenUid (Request parameter) Token.uid of the (new) Token object (to replace). max-length = 36
     * @param type (Request parameter) Token.type of the Token of the (new) Token object (to replace). Default if omitted:
     * RFID
     */
    suspend fun putToken(
        token: Token,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
    ): Token

    /**
     * PATCH Method
     *
     * Same as the PUT method, but only the fields/objects that have to be updated have to be present, other fields/objects that are not
     * specified are considered unchanged.
     *
     * Any request to the PATCH method SHALL contain the last_updated field.
     *
     * @param token (Request Body) New or updated Token object
     * @param countryCode (Request parameter) Country code of the eMSP sending this PUT request to the CPO system. This
     * SHALL be the same value as the country_code in the Token object being
     * pushed. max-length = 36
     * @param partyId (Request parameter) Party ID (Provider ID) of the eMSP sending this PUT request to the CPO
     * system. This SHALL be the same value as the party_id in the Token object
     * being pushed. max-length = 36
     * @param tokenUid (Request parameter) Token.uid of the (new) Token object (to replace). max-length = 36
     * @param type (Request parameter) Token.type of the Token of the (new) Token object (to replace). Default if omitted:
     * RFID
     */
    suspend fun patchToken(
        token: TokenPartial,
        countryCode: CiString,
        partyId: CiString,
        tokenUid: CiString,
        type: TokenType? = TokenType.RFID,
    ): Token?
}
