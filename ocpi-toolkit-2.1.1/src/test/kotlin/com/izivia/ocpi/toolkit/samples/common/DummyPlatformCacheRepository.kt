package com.izivia.ocpi.toolkit.samples.common

class DummyPlatformCacheRepository(private val tokenC: String): PlatformCacheRepository() {

    override fun getPlatformUrlByTokenC(token: String): String? = super.getPlatformUrlByTokenC(token)
        ?: (if (token == tokenC) "*" else null)

    override fun getCredentialsTokenC(platformUrl: String): String = super.getCredentialsTokenC(platformUrl)
        ?: tokenC
}
