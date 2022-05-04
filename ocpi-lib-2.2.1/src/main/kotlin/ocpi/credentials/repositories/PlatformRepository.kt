package ocpi.credentials.repositories

import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.Version

interface PlatformRepository {
    /**
     * Note: CREDENTIALS_TOKEN_A is used by the client to initiate registration.
     *
     * @param platformUrl
     * @return the CREDENTIALS_TOKEN_A for the given platform if it exists
     */
    fun getCredentialsTokenA(platformUrl: String): String?

    /**
     * Note: CREDENTIALS_TOKEN_C is used by the client to communicate with the server.
     *
     * @param platformUrl
     * @return the CREDENTIALS_TOKEN_C for the given platform if it exists
     */
    fun getCredentialsTokenC(platformUrl: String): String?

    fun getPlatformByTokenA(token: String): String?
    fun getPlatformByTokenB(token: String): String?
    fun getPlatformByTokenC(token: String): String?

    /**
     * Saves version used for the given platform
     *
     * @param platformUrl
     * @param version
     * @return the saved Version object
     */
    fun saveVersion(platformUrl: String, version: Version): Version

    /**
     * Saves available endpoints for the given platform
     *
     * @param platformUrl
     * @param endpoints
     * @return the saved endpoints list
     */
    fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint>

    /**
     * Saves CREDENTIALS_TOKEN_B for the given platform
     *
     * Note: CREDENTIALS_TOKEN_B is used by the server to communicate with the client.
     *
     * @param platformUrl
     * @param credentialsTokenB
     * @return the CREDENTIALS_TOKEN_B saved
     */
    fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String

    /**
     * Saves CREDENTIALS_TOKEN_C for the given platform
     *
     * Note: CREDENTIALS_TOKEN_C is used by the client to communicate with the server.
     *
     * @param platformUrl
     * @param credentialsTokenC
     * @return the CREDENTIALS_TOKEN_C saved
     */
    fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String

    /**
     * Removes CREDENTIALS_TOKEN_A from repository. Should be called when client finished registration with success.
     *
     * Note: CREDENTIALS_TOKEN_A is used by the client to initiate registration.
     *
     * @param platformUrl
     */
    fun removeCredentialsTokenA(platformUrl: String)
}