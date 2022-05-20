package ocpi.versions

import common.OcpiResponseBody
import ocpi.versions.domain.Version

interface VersionsInterface {
    fun getVersions(): OcpiResponseBody<List<Version>>
}