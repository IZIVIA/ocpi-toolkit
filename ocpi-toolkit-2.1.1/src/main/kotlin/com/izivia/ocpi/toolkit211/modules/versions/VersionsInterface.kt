package com.izivia.ocpi.toolkit211.modules.versions

import com.izivia.ocpi.toolkit211.modules.versions.domain.Version

interface VersionsInterface {
    suspend fun getVersions(): List<Version>
}
