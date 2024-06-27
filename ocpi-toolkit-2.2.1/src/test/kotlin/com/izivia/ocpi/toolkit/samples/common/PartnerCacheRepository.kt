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
        partnerUrl: String,
        credentialsRoles: List<CredentialRole>
    ): List<CredentialRole> = partners
        .getOrDefault(partnerUrl, Partner(partnerUrl))
        .copy(credentialsRoles = credentialsRoles)
        .also { partners[it.url!!] = it }
        .credentialsRoles

    override suspend fun saveVersion(partnerUrl: String, version: Version): Version = partners
        .getOrDefault(partnerUrl, Partner(partnerUrl))
        .copy(version = version)
        .also { partners[it.url!!] = it }
        .let { it.version!! }

    override suspend fun saveEndpoints(partnerUrl: String, endpoints: List<Endpoint>): List<Endpoint> = partners
        .getOrDefault(partnerUrl, Partner(partnerUrl))
        .copy(endpoints = endpoints)
        .also { partners[it.url!!] = it }
        .let { it.endpoints!! }

    override suspend fun saveCredentialsClientToken(partnerUrl: String, credentialsClientToken: String): String =
        partners
            .getOrDefault(partnerUrl, Partner(partnerUrl))
            .copy(clientToken = credentialsClientToken)
            .also { partners[it.url!!] = it }
            .let { it.clientToken!! }

    override suspend fun saveCredentialsServerToken(partnerUrl: String, credentialsServerToken: String): String =
        partners
            .getOrDefault(partnerUrl, Partner(partnerUrl))
            .copy(serverToken = credentialsServerToken)
            .also { partners[it.url!!] = it }
            .let { it.serverToken!! }

    override suspend fun getCredentialsClientToken(partnerUrl: String): String? = partners[partnerUrl]?.clientToken

    override suspend fun getCredentialsTokenA(partnerUrl: String): String? = partners[partnerUrl]?.tokenA

    override suspend fun isCredentialsTokenAValid(credentialsTokenA: String): Boolean = partners
        .any { it.tokenA == credentialsTokenA }

    override suspend fun isCredentialsServerTokenValid(credentialsServerToken: String): Boolean = partners
        .any { it.serverToken == credentialsServerToken }

    override suspend fun getPartnerUrlByCredentialsServerToken(credentialsServerToken: String): String? = partners
        .firstOrNull { it.serverToken == credentialsServerToken }?.url

    override suspend fun getEndpoints(partnerUrl: String): List<Endpoint> = partners
        .firstOrNull { it.url == partnerUrl }?.endpoints ?: emptyList()

    override suspend fun getVersion(partnerUrl: String): Version? = partners
        .firstOrNull { it.url == partnerUrl }?.version

    override suspend fun invalidateCredentialsTokenA(partnerUrl: String): Boolean =
        partners
            .getOrDefault(partnerUrl, Partner(partnerUrl))
            .copy(tokenA = null)
            .also { partners[it.url!!] = it }
            .let { true }

    override suspend fun invalidateCredentialsClientToken(partnerUrl: String): Boolean =
        partners
            .getOrDefault(partnerUrl, Partner(partnerUrl))
            .copy(clientToken = null)
            .also { partners[it.url!!] = it }
            .let { true }

    override suspend fun invalidateCredentialsServerToken(partnerUrl: String): Boolean =
        partners
            .getOrDefault(partnerUrl, Partner(partnerUrl))
            .copy(serverToken = null)
            .also { partners[it.url!!] = it }
            .let { true }
}
