package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

open class PlatformCacheRepository : PlatformRepository {
    val platforms = mutableListOf<Platform>()

    private fun List<Platform>.getOrDefault(url: String, platform: Platform): Platform =
        platforms[url] ?: platform

    private operator fun List<Platform>.get(url: String): Platform? =
        platforms.firstOrNull { it.url == url }

    private operator fun List<Platform>.set(url: String, platform: Platform) {
        platforms.removeAll { it.url == url }
        platforms.add(platform)
    }

    override suspend fun savePlatformUrlForTokenA(tokenA: String, platformUrl: String): String? = platforms
        .toList()
        .firstOrNull { it.tokenA == tokenA }
        ?.copy(url = platformUrl)
        ?.also { platforms[it.url!!] = it }
        ?.url

    override suspend fun saveCredentialsRoles(
        platformUrl: String,
        credentialsRoles: List<CredentialRole>
    ): List<CredentialRole> = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(credentialsRoles = credentialsRoles)
        .also { platforms[it.url!!] = it }
        .credentialsRoles

    override suspend fun saveVersion(platformUrl: String, version: Version): Version = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(version = version)
        .also { platforms[it.url!!] = it }
        .let { it.version!! }

    override suspend fun saveEndpoints(platformUrl: String, endpoints: List<Endpoint>): List<Endpoint> = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(endpoints = endpoints)
        .also { platforms[it.url!!] = it }
        .let { it.endpoints!! }

    override suspend fun saveCredentialsClientToken(platformUrl: String, credentialsClientToken: String): String =
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(clientToken = credentialsClientToken)
            .also { platforms[it.url!!] = it }
            .let { it.clientToken!! }

    override suspend fun saveCredentialsServerToken(platformUrl: String, credentialsServerToken: String): String =
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(serverToken = credentialsServerToken)
            .also { platforms[it.url!!] = it }
            .let { it.serverToken!! }

    override suspend fun getCredentialsClientToken(platformUrl: String): String? = platforms[platformUrl]?.clientToken

    override suspend fun getCredentialsTokenA(platformUrl: String): String? = platforms[platformUrl]?.tokenA

    override suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean = platforms
        .any { it.tokenA == credentialsTokenA }

    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean = platforms
        .any { it.serverToken == credentialsServerToken }

    override suspend fun getPlatformUrlByCredentialsServerToken(credentialsServerToken: String): String? = platforms
        .firstOrNull { it.serverToken == credentialsServerToken }?.url

    override suspend fun getEndpoints(platformUrl: String): List<Endpoint> = platforms
        .firstOrNull { it.url == platformUrl }?.endpoints ?: emptyList()

    override suspend fun getVersion(platformUrl: String): Version? = platforms
        .firstOrNull { it.url == platformUrl }?.version

    override suspend fun invalidateCredentialsTokenA(platformUrl: String): Boolean =
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(tokenA = null)
            .also { platforms[it.url!!] = it }
            .let { true }

    override suspend fun unregisterPlatform(platformUrl: String): Boolean {
        platforms[platformUrl] = Platform(platformUrl)
        return true
    }
}
