package ocpi.versions

import common.OcpiResponseBody
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails

interface VersionsInterface {
    fun getVersions(token: String): OcpiResponseBody<List<Version>>
    fun getVersionDetails(token: String, versionNumber: String): OcpiResponseBody<VersionDetails>
}