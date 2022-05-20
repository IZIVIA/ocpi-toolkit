package ocpi.versions.validation

import common.OcpiResponseBody
import common.OcpiServerUnsupportedVersionException
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.parseVersionNumber
import ocpi.versions.repositories.VersionDetailsRepository

class VersionDetailsValidationService(
    private val repository: VersionDetailsRepository
) {
    fun getVersionDetails(
        versionNumber: String
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}