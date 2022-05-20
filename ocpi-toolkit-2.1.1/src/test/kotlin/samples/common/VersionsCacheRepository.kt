package samples.common

import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionNumber
import ocpi.versions.repositories.VersionsRepository

class VersionsCacheRepository(
    private val baseUrl: String
): VersionsRepository {

    override fun getVersions(): List<Version> = listOf(
        Version(
            version = VersionNumber.V2_1_1.value,
            url = "$baseUrl/${VersionNumber.V2_1_1.value}"
        )
    )
}