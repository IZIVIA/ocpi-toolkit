package com.izivia.ocpi.toolkit.samples.common

class DummyPlatformCacheRepository(private val tokenC: String): PlatformCacheRepository() {

    override suspend fun getPlatformByTokenC(token: String): String? = super.getPlatformByTokenC(token)
        ?: (if (token == tokenC) "*" else null)

    override suspend fun getCredentialsTokenC(platformUrl: String): String = super.getCredentialsTokenC(platformUrl)
        ?: tokenC
}
