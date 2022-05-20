package com.izivia.ocpi.toolkit.modules.versions.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.Version

interface VersionsRepository {
    fun getVersions(): List<Version>
}