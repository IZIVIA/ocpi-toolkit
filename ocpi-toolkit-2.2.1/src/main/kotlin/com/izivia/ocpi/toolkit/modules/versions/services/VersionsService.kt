package com.izivia.ocpi.toolkit.modules.versions.services

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.OcpiServerUnsupportedVersionException
import com.izivia.ocpi.toolkit.modules.versions.VersionsInterface
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.parseVersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionsRepository

open class VersionsService(
    private val repository: VersionsRepository,
    private val baseUrlProvider: suspend () -> String,
    val versionsBasePath: String = "/versions",
    val versionDetailsBasePath: String = "",
) : VersionsInterface {

    override suspend fun getVersions(): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        repository.getVersions().map {
            Version(
                version = it.value,
                url = "${baseUrlProvider()}$versionDetailsBasePath/${it.value}",
            )
        }
    }

    suspend fun getVersionDetails(
        versionNumber: String,
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}
