package com.izivia.ocpi.toolkit.samples.common

class DummyPlatformCacheRepository : PlatformCacheRepository() {
    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean =
        true

    override suspend fun getCredentialsClientToken(platformUrl: String): String =
        "*"
}
