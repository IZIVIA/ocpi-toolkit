package com.izivia.ocpi.toolkit.modules.versions.services

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.versions.VersionsInterface
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.repositories.VersionsRepository

class VersionsService(
    private val repository: VersionsRepository
) : VersionsInterface {

    override suspend fun getVersions(): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        repository.getVersions()
    }
}
