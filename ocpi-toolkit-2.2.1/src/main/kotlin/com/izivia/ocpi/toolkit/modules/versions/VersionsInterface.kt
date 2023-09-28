package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.versions.domain.Version

interface VersionsInterface {
    suspend fun getVersions(): OcpiResponseBody<List<Version>>
}
