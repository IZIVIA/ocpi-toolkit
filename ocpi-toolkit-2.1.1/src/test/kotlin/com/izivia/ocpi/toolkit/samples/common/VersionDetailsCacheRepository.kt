package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionDetailsRepository

class VersionDetailsCacheRepository(
    private val baseUrl: String
): VersionDetailsRepository {

    override fun getVersionDetails(versionNumber: VersionNumber): VersionDetails? =
        VersionDetails(
            version = VersionNumber.V2_1_1.value,
            endpoints = listOf(
                Endpoint(
                    identifier = ModuleID.credentials,
                    url = "$baseUrl/credentials"
                ),
                Endpoint(
                    identifier = ModuleID.locations,
                    url = "$baseUrl/locations"
                )
            )
        ).takeIf { versionNumber.value == it.version }
}