package com.izivia.ocpi.toolkit.samples.locations

import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.samples.common.VersionDetailsCacheRepository

const val CREDENTIALS_TOKEN_C = "e0748bbe-d535-4a6b-8f80-94f2457d5b9d"
val DUMMY_PLATFORM_REPOSITORY = com.izivia.ocpi.toolkit.samples.common.DummyPlatformCacheRepository(tokenC = CREDENTIALS_TOKEN_C)
    .also {
    val versionDetailsEmsp = VersionDetailsCacheRepository(baseUrl = emspServerUrl)
    val versionDetailsCpo = VersionDetailsCacheRepository(baseUrl = cpoServerUrl)

    it.saveEndpoints(
        emspServerVersionsUrl,
        versionDetailsEmsp.getVersionDetails(VersionNumber.V2_1_1)!!.endpoints
    )

    it.saveEndpoints(
        cpoServerVersionsUrl,
        versionDetailsCpo.getVersionDetails(VersionNumber.V2_1_1)!!.endpoints
    )
}