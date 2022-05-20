package com.izivia.ocpi.toolkit.modules.versions.validation

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.versions.VersionsInterface
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionsRepository

class VersionsValidationService(
    private val repository: VersionsRepository
) : VersionsInterface {

    override fun getVersions(): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        repository.getVersions()
    }
}