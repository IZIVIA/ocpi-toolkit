package com.izivia.ocpi.toolkit.modules.versions.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber

interface MutableVersionsRepository : VersionsRepository {
    suspend fun addEndpoint(versionNumber: VersionNumber, endpoint: Endpoint)
}
