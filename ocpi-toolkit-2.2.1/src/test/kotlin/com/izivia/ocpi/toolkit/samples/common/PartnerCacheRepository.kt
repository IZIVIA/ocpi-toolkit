package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

open class PartnerCacheRepository : PartnerRepository {
    val partners = mutableListOf<Partner>()

    private fun List<Partner>.getOrDefault(url: String, partner: Partner): Partner =
        partners[url] ?: partner

    private operator fun List<Partner>.get(url: String): Partner? =
        partners.firstOrNull { it.url == url }

    private operator fun List<Partner>.set(url: String, partner: Partner) {
        partners.removeAll { it.url == url }
        partners.add(partner)
    }

    override suspend fun savePartnerUrlForTokenA(tokenA: String, partnerUrl: String): String? = partners
        .toList()
        .indexOfFirst { it.tokenA == tokenA }
        .let { index ->
            partners[index] = partners[index].copy(url = partnerUrl)
            partnerUrl
        }

    override suspend fun saveCredentialsRoles(
        partnerId: String,
        credentialsRoles: List<CredentialRole>,
    ): List<CredentialRole> = partners
        .getOrDefault(partnerId, Partner(partnerId))
        .copy(credentialsRoles = credentialsRoles)
        .also { partners[it.url!!] = it }
        .credentialsRoles

    override suspend fun saveVersion(partnerId: String, version: Version): Version = partners
        .getOrDefault(partnerId, Partner(partnerId))
        .copy(version = version)
        .also { partners[it.url!!] = it }
        .let { it.version!! }

    override suspend fun saveEndpoints(partnerId: String, endpoints: List<Endpoint>): List<Endpoint> = partners
        .getOrDefault(partnerId, Partner(partnerId))
        .copy(endpoints = endpoints)
        .also { partners[it.url!!] = it }
        .let { it.endpoints!! }

    override suspend fun saveCredentialsClientToken(partnerId: String, credentialsClientToken: String): String =
        partners
            .getOrDefault(partnerId, Partner(partnerId))
            .copy(clientToken = credentialsClientToken)
            .also { partners[it.url!!] = it }
            .let { it.clientToken!! }

    override suspend fun saveCredentialsServerToken(partnerId: String, credentialsServerToken: String): String =
        partners
            .getOrDefault(partnerId, Partner(partnerId))
            .copy(serverToken = credentialsServerToken)
            .also { partners[it.url!!] = it }
            .let { it.serverToken!! }

    override suspend fun getCredentialsClientToken(partnerId: String): String? = partners[partnerId]?.clientToken

    override suspend fun getCredentialsTokenA(partnerId: String): String? = partners[partnerId]?.tokenA

    override suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean = partners
        .any { it.tokenA == credentialsTokenA }

    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean = partners
        .any { it.serverToken == credentialsServerToken }

    override suspend fun getPartnerUrl(partnerId: String): String? = partners
        .firstOrNull { it.url == partnerId }?.url

    override suspend fun getPartnerUrlByCredentialsServerToken(credentialsServerToken: String): String? = partners
        .firstOrNull { it.serverToken == credentialsServerToken }?.url

    override suspend fun getPartnerIdByCredentialsServerToken(credentialsServerToken: String): String? = partners
        .firstOrNull { it.serverToken == credentialsServerToken }?.url

    override suspend fun getPartnerIdByCredentialsTokenA(credentialsTokenA: String): String? = partners
        .firstOrNull { it.tokenA == credentialsTokenA }?.url

    override suspend fun getEndpoints(partnerId: String): List<Endpoint> = partners
        .firstOrNull { it.url == partnerId }?.endpoints ?: emptyList()

    override suspend fun getVersion(partnerId: String): Version? = partners
        .firstOrNull { it.url == partnerId }?.version

    override suspend fun invalidateCredentialsTokenA(partnerId: String): Boolean =
        partners
            .getOrDefault(partnerId, Partner(partnerId))
            .copy(tokenA = null)
            .also { partners[it.url!!] = it }
            .let { true }

    override suspend fun invalidateCredentialsClientToken(partnerId: String): Boolean =
        partners
            .getOrDefault(partnerId, Partner(partnerId))
            .copy(clientToken = null)
            .also { partners[it.url!!] = it }
            .let { true }

    override suspend fun invalidateCredentialsServerToken(partnerId: String): Boolean =
        partners
            .getOrDefault(partnerId, Partner(partnerId))
            .copy(serverToken = null)
            .also { partners[it.url!!] = it }
            .let { true }
}
