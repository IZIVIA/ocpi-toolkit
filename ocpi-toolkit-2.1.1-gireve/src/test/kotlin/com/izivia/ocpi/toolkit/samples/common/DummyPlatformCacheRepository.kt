package com.izivia.ocpi.toolkit.samples.common

class DummyPlatformCacheRepository(private val tokenC: String): com.izivia.ocpi.toolkit.samples.common.PlatformCacheRepository() {

    override fun getPlatformByTokenC(token: String): String? = super.getPlatformByTokenC(token)
        ?: (if (token == tokenC) "*" else null)

    override fun getCredentialsTokenC(platformUrl: String): String = super.getCredentialsTokenC(platformUrl)
        ?: tokenC
}