package com.izivia.ocpi.toolkit.modules.tokens

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenPartial

/**
 * With this interface the eMSP can push the Token information to the CPO. Tokens is a client owned object, so the
 * end-points need to contain the required extra fields: {party_id} and {country_code}. Example endpoint structure:
 * /ocpi/cpo/2.0/tokens/{country_code}/{party_id}/{token_uid}
 *
 * - GET: Retrieve a Token as it is stored in the CPO system.
 * - POST: n/a
 * - PUT: Push new/updated Token object to the CPO.
 * - PATCH: Notify the CPO of partial updates to a Token.
 * - DELETE: n/a, (Use PUT, Tokens cannot be removed).
 */
interface TokensCpoInterface {

    /**
     * If the eMSP wants to check the status of a Token in the CPO system it might GET the object from the CPO system
     * for validation purposes. The eMSP is the owner of the objects, so it would be illogical if the CPO system had a
     * different status or was missing an object.
     *
     * @param countryCode (max-length 2) Country code of the eMSP requesting this GET from the CPO system.
     * @param partyId (max-length 3) 	Party ID (Provider ID) of the eMSP requesting this GET from the CPO system.
     * @param tokenUid (max-length 36) Token.uid of the Token object to retrieve.
     * @return The requested Token object.
     */
    fun getToken(
        countryCode: String,
        partyId: String,
        tokenUid: String
    ): OcpiResponseBody<Token?>

    /**
     * New or updated Token objects are pushed from the eMSP to the CPO.
     *
     * @param countryCode (max-length 2) Country code of the eMSP sending this PUT request to the CPO system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the eMSP sending this PUT request to the CPO system.
     * @param tokenUid (max-length 36) Token.uid of the (new) Token object (to replace).
     * @param token New or updated Token object.
     */
    fun putToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: Token
    ): OcpiResponseBody<Token>

    /**
     * Same as the PUT method, but only the fields/objects that have to be updated have to be present, other
     * fields/objects that are not specified are considered unchanged.
     *
     * @param countryCode (max-length 2) Country code of the eMSP sending this PUT request to the CPO system.
     * @param partyId (max-length 3) Party ID (Provider ID) of the eMSP sending this PUT request to the CPO system.
     * @param tokenUid (max-length 36) Token.uid of the (new) Token object (to replace).
     * @param token New or updated Token object.
     */
    fun patchToken(
        countryCode: String,
        partyId: String,
        tokenUid: String,
        token: TokenPartial
    ): OcpiResponseBody<Token>
}
