package samples.common

import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.ModuleID
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.VersionNumber
import ocpi.versions.repositories.VersionDetailsRepository

class VersionDetailsCacheRepository(
    private val baseUrl: String
): VersionDetailsRepository {

    override fun getVersionDetails(versionNumber: VersionNumber): VersionDetails? =
        VersionDetails(
            version = VersionNumber.V2_1_1.value,
            endpoints = listOf(
                Endpoint(
                    identifier = ModuleID.credentials,
                    url = "$baseUrl/credentials"
                ),
                Endpoint(
                    identifier = ModuleID.locations,
                    url = "$baseUrl/locations"
                )
            )
        ).takeIf { versionNumber.value == it.version }
}