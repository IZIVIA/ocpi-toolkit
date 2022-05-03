package ocpi.versions.repositories

import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.VersionNumber

interface VersionsRepository {
    fun getVersions(): List<Version>
    fun getVersionDetails(versionNumber: VersionNumber): VersionDetails
}