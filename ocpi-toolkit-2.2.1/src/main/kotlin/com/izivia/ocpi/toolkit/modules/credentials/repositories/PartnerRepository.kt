package com.izivia.ocpi.toolkit.modules.credentials.repositories

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

/**
 * **Note about tokens:**
 * The Credentials Token A is used by the sender to communicate with the receiver (only when initiating registration).
 * After registration, it is invalidated.
 *
 * Then, there are two tokens, and the token to use is specified by the registration process.
 * - When you are the receiver during registration:
 *   - OCPI Token B is the client token, the one you have to use to send requests
 *   - OCPI Token C is the server token, the one you have to use to check if requests are properly authenticated
 * - When you are the sender during registration:
 *   - OCPI Token B is the server token, the one you have to use to check if requests are properly authenticated
 *   - OCPI Token C is the client token, the one you have to use to send requests
 *
 * This is why we do not use OCPI's token B and token C naming. According to who you are in the registration process,
 * you have to use a different token. Using "Client Token" and "Server Token" simplifies that process of picking the
 * right token for the right operation.
 */
interface PartnerRepository {

    /**
     * When calling a partner, the http client has to be authenticated. This method is called to retrieve the
     * token A for a given partner identified by its /versions url.
     *
     * The token A is only used during registration process.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return the token A, if found, null otherwise
     */
    suspend fun getCredentialsTokenA(partnerUrl: String): String?

    /**
     * When calling a partner, the http client has to be authenticated. This method is called to retrieve the
     * client token for a given partner identified by its /versions url.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return the client token, if found, null otherwise
     */
    suspend fun getCredentialsClientToken(partnerUrl: String): String?

    /**
     * A partner has to use its server token to communicate with us. On first registration, the token used is the token
     * A. This method is called to check if the token A of a partner is valid.
     *
     * @param credentialsTokenA the token of a partner
     * @return true if the token is valid (exists), false otherwise
     */
    suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean

    /**
     * A partner has to use its server token to communicate with us. This method is called to check if the token of a
     * partner is valid.
     *
     * @param credentialsServerToken the token of a partner
     * @return true if the token is valid (exists), false otherwise
     */
    suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean

    /**
     * Used to find a partner url by its server token. Basically used to retrieve all the partner information
     * from the token in a request.
     *
     * @param credentialsServerToken the server token, the one partners use to communicate
     * @return the partner url
     */
    suspend fun getPartnerUrlByCredentialsServerToken(credentialsServerToken: String): String?

    /**
     * Used to get available endpoints for a given partner identified by its url (partnerUrl)
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return the list of available endpoints for the given partner
     */
    suspend fun getEndpoints(partnerUrl: String): List<Endpoint>

    /**
     * Used to get used version for a given partner identified by its url (partnerUrl)
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return the currently used version for the given partner or null
     */
    suspend fun getVersion(partnerUrl: String): Version?

    /**
     * This is the first function to be called on registration. So that later on we can use partner url as an
     * identifier for a given partner.
     *
     * It searches for a partner with the given token A and saves the corresponding partnerUrl
     *
     * @param tokenA
     * @param partnerUrl the partner, identified by its /versions url
     * @return the partnerUrl if a partner was found for given token A and the update was a success, null otherwise
     */
    suspend fun savePartnerUrlForTokenA(tokenA: String, partnerUrl: String): String?

    /**
     * Used to save credentials roles given by a partner during registration.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @param credentialsRoles
     * @return the updated credentials roles
     */
    suspend fun saveCredentialsRoles(partnerUrl: String, credentialsRoles: List<CredentialRole>): List<CredentialRole>

    /**
     * Used to save available version for a given partner identified by its url (partnerUrl)
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @param version Version
     * @return the updated version
     */
    suspend fun saveVersion(partnerUrl: String, version: Version): Version

    /**
     * Used to save endpoints for a given partner identified by its url (partnerUrl)
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @param endpoints List<Endpoint>
     * @return List<Endpoint> the updated list of endpoints
     */
    suspend fun saveEndpoints(partnerUrl: String, endpoints: List<Endpoint>): List<Endpoint>

    /**
     * Called to save the client token for a given partner identified by its url (partnerUrl).
     *
     * This token is the one that will be used to communicate with the partner.
     * For context in OCPI, on registration, this token is:
     * - if you are the receiver: token B
     * - if you are the sender: token C
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @param credentialsClientToken String
     * @return the credentialsClientToken
     */
    suspend fun saveCredentialsClientToken(partnerUrl: String, credentialsClientToken: String): String

    /**
     * Called to save the server token for a given partner identified by its url (partnerUrl).
     *
     * This token is the one that the partner will use to communicate. So it is this token that has to be used to check
     * if requests are properly authenticated.
     *
     * For context in OCPI, on registration, this token is:
     * - if you are the receiver: token C
     * - if you are the sender: token B
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @param credentialsServerToken String
     * @return the credentialsServerToken
     */
    suspend fun saveCredentialsServerToken(partnerUrl: String, credentialsServerToken: String): String

    /**
     * Called once registration is done for a given partner identified by its url (partnerUrl).
     *
     * The token A has to be invalidated.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return true if it was a success, false otherwise
     */
    suspend fun invalidateCredentialsTokenA(partnerUrl: String): Boolean

    /**
     * Called when a partner asks to unregister itself.
     *
     * It has to invalidate **ONLY clientToken**. ServerToken must stay valid because the partner can still use it.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return true if it was a success, false otherwise
     */
    suspend fun invalidateCredentialsClientToken(partnerUrl: String): Boolean

    /**
     * Called when your system unregisters from a partner.
     *
     * It has to invalidate **ONLY serverToken**. ClientToken must stay valid because your system can still use it
     * to communicate with the partner.
     *
     * @param partnerUrl the partner, identified by its /versions url
     * @return true if it was a success, false otherwise
     */
    suspend fun invalidateCredentialsServerToken(partnerUrl: String): Boolean
}
