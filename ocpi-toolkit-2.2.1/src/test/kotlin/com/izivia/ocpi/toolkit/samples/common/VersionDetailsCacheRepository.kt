package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.versions.domain.*
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionDetailsRepository

class VersionDetailsCacheRepository(
    private val baseUrl: String
): VersionDetailsRepository {

    override suspend fun getVersionDetails(versionNumber: VersionNumber): VersionDetails? =
        VersionDetails(
            version = VersionNumber.V2_2_1.value,
            endpoints = listOf(
                Endpoint(
                    identifier = ModuleID.credentials,
                    role = InterfaceRole.RECEIVER,
                    url = "$baseUrl/${VersionNumber.V2_2_1.value}/credentials"
                ),
                Endpoint(
                    identifier = ModuleID.locations,
                    role = InterfaceRole.RECEIVER,
                    url = "$baseUrl/${VersionNumber.V2_2_1.value}/locations"
                )
            )
        ).takeIf { versionNumber.value == it.version }
}
