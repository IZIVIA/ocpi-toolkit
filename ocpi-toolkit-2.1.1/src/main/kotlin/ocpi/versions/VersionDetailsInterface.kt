package ocpi.versions

import common.OcpiResponseBody
import ocpi.versions.domain.VersionDetails

interface VersionDetailsInterface {
    fun getVersionDetails(): OcpiResponseBody<VersionDetails>
}