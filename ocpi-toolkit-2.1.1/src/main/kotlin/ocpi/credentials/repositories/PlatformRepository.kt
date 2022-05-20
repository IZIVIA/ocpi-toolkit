package ocpi.credentials.repositories

import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.Version

/**
 * - CREDENTIALS_TOKEN_A: used by the client to communicate with the server (when initiating registration).
 * - CREDENTIALS_TOKEN_B: used by the server to communicate with the client (during registration).
 * - CREDENTIALS_TOKEN_C: used by the client to communicate with the server (once registered).
 */
interface PlatformRepository {
    fun getCredentialsTokenA(platformUrl: String): String?
    fun getCredentialsTokenC(platformUrl: String): String?
    fun getPlatformByTokenA(token: String): String?
    fun getPlatformByTokenB(token: String): String?
    fun getPlatformByTokenC(token: String): String?
    fun getEndpoints(platformUrl: String): List<Endpoint>
    fun getVersion(platformUrl: String): Version?
    fun saveVersion(platformUrl: String, version: Version): Version
    fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint>
    fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String
    fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String
    fun removeCredentialsTokenA(platformUrl: String)
    fun removeCredentialsTokenC(platformUrl: String)
    fun removeVersion(platformUrl: String)
    fun removeEndpoints(platformUrl: String)
}