package samples.locations

import ocpi.versions.domain.VersionNumber
import samples.common.DummyPlatformCacheRepository
import samples.common.VersionDetailsCacheRepository

const val CREDENTIALS_TOKEN_C = "e0748bbe-d535-4a6b-8f80-94f2457d5b9d"
val DUMMY_PLATFORM_REPOSITORY = DummyPlatformCacheRepository(tokenC = CREDENTIALS_TOKEN_C).also {
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