package com.izivia.ocpi.toolkit.samples.common

class DummyPartnerCacheRepository : PartnerCacheRepository() {
    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean =
        true

    override suspend fun getCredentialsClientToken(partnerUrl: String): String =
        "*"
}
