package com.izivia.ocpi.toolkit.modules.versions.validation

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.OcpiServerUnsupportedVersionException
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.parseVersionNumber
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionDetailsRepository

class VersionDetailsValidationService(
    private val repository: VersionDetailsRepository
) {
    fun getVersionDetails(
        versionNumber: String
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}