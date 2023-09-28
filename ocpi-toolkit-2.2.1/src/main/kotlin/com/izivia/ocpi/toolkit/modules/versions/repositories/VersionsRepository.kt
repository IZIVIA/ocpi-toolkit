package com.izivia.ocpi.toolkit.modules.versions.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.Version

interface VersionsRepository {
    suspend fun getVersions(): List<Version>
}
