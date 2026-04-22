package com.izivia.ocpi.toolkit211.modules.versions.repositories

import com.izivia.ocpi.toolkit211.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionNumber

interface MutableVersionsRepository : VersionsRepository {
    suspend fun addEndpoint(versionNumber: VersionNumber, endpoint: Endpoint)
}
