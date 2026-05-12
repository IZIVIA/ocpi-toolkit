package com.izivia.ocpi.toolkit211.modules.versions

import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails

interface VersionDetailsInterface {
    suspend fun getVersionDetails(): VersionDetails
}
