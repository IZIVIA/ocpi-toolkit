package com.izivia.ocpi.toolkit.samples.common

import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionsRepository

class VersionsCacheRepository(
    private val baseUrl: String
): VersionsRepository {

    override fun getVersions(): List<Version> = listOf(
        Version(
            version = VersionNumber.V2_1_1.value,
            url = "$baseUrl/${VersionNumber.V2_1_1.value}"
        )
    )
}