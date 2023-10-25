package com.izivia.ocpi.toolkit.samples.common

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

    override suspend fun saveCredentialsTokenA(platformUrl: String, credentialsTokenA: String): String = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(tokenA = credentialsTokenA)
        .also { platforms[it.url!!] = it }
        .let { it.tokenA!! }

    override suspend fun saveCredentialsTokenB(platformUrl: String, credentialsTokenB: String): String = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(tokenB = credentialsTokenB)
        .also { platforms[it.url!!] = it }
        .let { it.tokenB!! }

    override suspend fun saveCredentialsTokenC(platformUrl: String, credentialsTokenC: String): String = platforms
        .getOrDefault(platformUrl, Platform(platformUrl))
        .copy(tokenC = credentialsTokenC)
        .also { platforms[it.url!!] = it }
        .let { it.tokenC!! }

    override suspend fun getCredentialsTokenC(platformUrl: String): String? = platforms[platformUrl]?.tokenC

    override suspend fun getCredentialsTokenB(platformUrl: String): String? = platforms[platformUrl]?.tokenB

    override suspend fun getCredentialsTokenA(platformUrl: String): String? = platforms[platformUrl]?.tokenA

    override suspend fun platformExistsWithTokenA(token: String): Boolean = platforms
        .any { it.tokenA == token }

    override suspend fun platformExistsWithTokenB(token: String): Boolean = platforms
        .any { it.tokenB == token }

    override suspend fun getPlatformUrlByTokenC(token: String): String? = platforms
        .firstOrNull { it.tokenC == token }?.url

    override suspend fun getEndpoints(platformUrl: String): List<Endpoint> = platforms
        .firstOrNull { it.url == platformUrl }?.endpoints ?: emptyList()

    override suspend fun getVersion(platformUrl: String): Version? = platforms
        .firstOrNull { it.url == platformUrl }?.version

    override suspend fun removeCredentialsTokenA(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(tokenA = null)
            .also { platforms[it.url!!] = it }
    }

    override suspend fun removeCredentialsTokenB(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(tokenB = null)
            .also { platforms[it.url!!] = it }
    }

    override suspend fun removeCredentialsTokenC(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(tokenC = null)
            .also { platforms[it.url!!] = it }
    }

    override suspend fun removeVersion(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(version = null)
            .also { platforms[it.url!!] = it }
    }

    override suspend fun removeEndpoints(platformUrl: String) {
        platforms
            .getOrDefault(platformUrl, Platform(platformUrl))
            .copy(endpoints = null)
            .also { platforms[it.url!!] = it }
    }
}
