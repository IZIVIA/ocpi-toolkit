package com.izivia.ocpi.toolkit211.modules.versions.services

import com.izivia.ocpi.toolkit211.common.OcpiServerUnsupportedVersionException
import com.izivia.ocpi.toolkit211.modules.versions.VersionsInterface
import com.izivia.ocpi.toolkit211.modules.versions.domain.Version
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit211.modules.versions.domain.parseVersionNumber
import com.izivia.ocpi.toolkit211.modules.versions.repositories.VersionsRepository

open class VersionsService(
    private val repository: VersionsRepository,
    private val baseUrlProvider: suspend () -> String,
    val versionsBasePath: String = "/versions",
    val versionDetailsBasePath: String = "",
) : VersionsInterface {

    override suspend fun getVersions(): List<Version> {
        return repository.getVersions().map {
            Version(
                version = it.value,
                url = "${baseUrlProvider()}$versionDetailsBasePath/${it.value}",
            )
        }
    }

    suspend fun getVersionDetails(
        versionNumber: String,
    ): VersionDetails {
        return parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}
