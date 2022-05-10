package ocpi.versions

import common.OcpiResponseBody
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails

interface VersionsInterface {
    fun getVersions(): OcpiResponseBody<List<Version>>
    fun getVersionDetails(versionNumber: String): OcpiResponseBody<VersionDetails>
}