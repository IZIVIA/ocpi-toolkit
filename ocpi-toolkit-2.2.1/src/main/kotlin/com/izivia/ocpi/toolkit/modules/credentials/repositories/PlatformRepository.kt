package com.izivia.ocpi.toolkit.modules.credentials.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

/**
 * - CREDENTIALS_TOKEN_A: used by the client to communicate with the server (when initiating registration).
 * - CREDENTIALS_TOKEN_B: used by the server to communicate with the client (during registration).
 * - CREDENTIALS_TOKEN_C: used by the client to communicate with the server (once registered).
 */
interface PlatformRepository {
    suspend fun getCredentialsTokenA(platformUrl: String): String?
    suspend fun getCredentialsTokenB(platformUrl: String): String?
    suspend fun getCredentialsTokenC(platformUrl: String): String?
    suspend fun getPlatformByTokenA(token: String): String?
    suspend fun getPlatformByTokenB(token: String): String?
    suspend fun getPlatformByTokenC(token: String): String?
    suspend fun getEndpoints(platformUrl: String): List<Endpoint>
    suspend fun getVersion(platformUrl: String): Version?
    suspend fun savePlatformUrlForTokenA(tokenA: String, platformUrl: String): String?
    suspend fun saveVersion(platformUrl: String, version: Version): Version
    suspend fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint>
    suspend fun saveCredentialsTokenA(platformUrl: String, credentialsTokenA: String): String
    suspend fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String
    suspend fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String
    suspend fun removeCredentialsTokenA(platformUrl: String)
    suspend fun removeCredentialsTokenB(platformUrl: String)
    suspend fun removeCredentialsTokenC(platformUrl: String)
    suspend fun removeVersion(platformUrl: String)
    suspend fun removeEndpoints(platformUrl: String)
}
