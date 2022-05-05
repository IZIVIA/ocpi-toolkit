package samples.common

import ocpi.versions.domain.*
import ocpi.versions.repositories.VersionsRepository

class VersionsCacheRepository(
    private val baseUrl: String
): VersionsRepository {

    override fun getVersions(): List<Version> = listOf(
        Version(
            version = VersionNumber.V2_2_1,
            url = "$baseUrl/${VersionNumber.V2_2_1.value}"
        )
    )

    override fun getVersionDetails(versionNumber: VersionNumber): VersionDetails? =
        VersionDetails(
            version = VersionNumber.V2_2_1,
            endpoints = listOf(
                Endpoint(
                    identifier = ModuleID.credentials,
                    role = InterfaceRole.RECEIVER,
                    url = "$baseUrl/credentials"
                )
            )
        ).takeIf { versionNumber == it.version }
}