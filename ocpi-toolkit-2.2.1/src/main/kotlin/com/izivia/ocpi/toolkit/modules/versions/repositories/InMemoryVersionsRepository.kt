package com.izivia.ocpi.toolkit.modules.versions.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber

class InMemoryVersionsRepository : MutableVersionsRepository {

    private val versions: MutableMap<VersionNumber, MutableList<Endpoint>> = mutableMapOf()

    override suspend fun addEndpoint(versionNumber: VersionNumber, endpoint: Endpoint) {
        val endpoints = versions.getOrPut(versionNumber) { mutableListOf() }

        endpoints.find { it.identifier == endpoint.identifier && it.role == endpoint.role }
            ?.let { throw Exception("duplicate module already registered") }

        endpoints.add(endpoint)
    }

    override suspend fun getVersions(): List<VersionNumber> {
        return versions.keys.toList()
    }

    override suspend fun getVersionDetails(versionNumber: VersionNumber): VersionDetails? {
        return versions[versionNumber]?.let {
            VersionDetails(
                version = versionNumber.value,
                endpoints = it,
            )
        }
    }
}
