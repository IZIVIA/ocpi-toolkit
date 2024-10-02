package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials

/**
 * The Credentials module is different from all other OCPI modules. This module is symmetric, it has to be implemented
 * by all OCPI implementations, and all implementations need to be able call this module on any other partner, and have
 * to be able the handle receiving the request from another party.
 */
interface CredentialsInterface {
    /**
     * Retrieves the credentials object to access the server’s platform. The request body is empty, the response
     * contains the credentials object to access the server’s platform. This credentials object also contains extra
     * information about the server such as its business details.
     */
    suspend fun get(token: String): OcpiResponseBody<Credentials>

    /**
     * Provides the server with credentials to access the client's system. This credentials object also contains extra
     * information about the client such as its business details.
     *
     * A POST initiates the registration process for this endpoint's version. The server must also fetch the client's
     * endpoints for this version. If successful, the server must generate a new credentials token and respond with the
     * client's new credentials to access the server's system. The credentials object in the response also contains
     * extra information about the server such as its business details.
     *
     * This method MUST return a HTTP status code 405: method not allowed if the client has already been registered
     * before.
     */
    suspend fun post(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): OcpiResponseBody<Credentials>

    /**
     * Provides the server with updated credentials to access the client’s system. This credentials object also contains
     * extra information about the client such as its business details.
     *
     * A PUT will switch to the version that contains this credentials endpoint if it’s different from the current
     * version. The server must fetch the client’s endpoints again, even if the version has not changed.
     *
     * If successful, the server must generate a new credentials token for the client and respond with the client’s
     * updated credentials to access the server’s system. The credentials object in the response also contains extra
     * information about the server such as its business details.
     *
     * This method MUST return a HTTP status code 405: method not allowed if the client has not been registered yet.
     */
    suspend fun put(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): OcpiResponseBody<Credentials>

    /**
     * Informs the server that its credentials to access the client’s system are now invalid and can no longer be used.
     * Both parties must end any automated communication. This is the unregistration process.
     *
     * This method MUST return a HTTP status code 405: method not allowed if the client has not been registered before.
     */
    suspend fun delete(token: String): OcpiResponseBody<Credentials?>
}
