package ocpi.versions.repositories

import ocpi.versions.domain.Version

interface VersionsRepository {
    fun getVersions(): List<Version>
}