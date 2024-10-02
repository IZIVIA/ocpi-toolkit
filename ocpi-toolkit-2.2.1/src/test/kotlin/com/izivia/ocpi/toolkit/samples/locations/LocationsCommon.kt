package com.izivia.ocpi.toolkit.samples.locations

import com.izivia.ocpi.toolkit.modules.versions.domain.VersionNumber
import com.izivia.ocpi.toolkit.samples.common.DummyPartnerCacheRepository
import com.izivia.ocpi.toolkit.samples.common.VersionsCacheRepository
import kotlinx.coroutines.runBlocking

const val CREDENTIALS_TOKEN_C = "e0748bbe-d535-4a6b-8f80-94f2457d5b9d"
val DUMMY_PLATFORM_REPOSITORY = DummyPartnerCacheRepository().also {
    val versionDetailsEmsp = VersionsCacheRepository(baseUrl = emspServerUrl)
    val versionDetailsCpo = VersionsCacheRepository(baseUrl = cpoServerUrl)

    runBlocking {
        it.saveEndpoints(
            emspServerVersionsUrl,
            versionDetailsEmsp.getVersionDetails(VersionNumber.V2_2_1)!!.endpoints,
        )

        it.saveEndpoints(
            cpoServerVersionsUrl,
            versionDetailsCpo.getVersionDetails(VersionNumber.V2_2_1)!!.endpoints,
        )
    }
}
