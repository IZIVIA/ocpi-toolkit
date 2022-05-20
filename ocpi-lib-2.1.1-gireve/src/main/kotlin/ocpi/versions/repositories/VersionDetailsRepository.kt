package ocpi.versions.repositories

import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.VersionNumber

interface VersionDetailsRepository {
    fun getVersionDetails(versionNumber: VersionNumber): VersionDetails?
}