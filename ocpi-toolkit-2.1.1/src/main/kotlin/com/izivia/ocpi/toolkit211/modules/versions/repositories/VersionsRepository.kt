package com.izivia.ocpi.toolkit211.modules.versions.repositories

import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber

interface VersionsRepository {
    suspend fun getVersions(): List<VersionNumber>
    suspend fun getVersionDetails(versionNumber: VersionNumber): VersionDetails?
}
