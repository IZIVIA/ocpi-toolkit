package com.izivia.ocpi.toolkit.modules.versions.repositories

import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber

interface VersionDetailsRepository {
    fun getVersionDetails(versionNumber: VersionNumber): VersionDetails?
}