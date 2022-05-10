package ocpi.versions.validation

import common.OcpiResponseBody
import common.OcpiServerUnsupportedVersionException
import ocpi.versions.VersionsInterface
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.parseVersionNumber
import ocpi.versions.repositories.VersionsRepository

class VersionsValidationService(
    private val repository: VersionsRepository
) : VersionsInterface {

    override fun getVersions(): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        repository.getVersions()
    }

    override fun getVersionDetails(
        versionNumber: String
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}