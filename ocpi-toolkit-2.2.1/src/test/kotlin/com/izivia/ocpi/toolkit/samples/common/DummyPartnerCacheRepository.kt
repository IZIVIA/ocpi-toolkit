package com.izivia.ocpi.toolkit.samples.common

class DummyPartnerCacheRepository : PartnerCacheRepository() {
    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean =
        true

    override suspend fun getPartnerUrl(partnerId: String): String = partnerId

    override suspend fun getCredentialsClientToken(partnerId: String): String =
        "*"
}
