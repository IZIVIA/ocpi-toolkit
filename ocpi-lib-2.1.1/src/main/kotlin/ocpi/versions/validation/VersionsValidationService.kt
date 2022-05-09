package ocpi.versions.validation

import common.OcpiResponseBody
import common.OcpiServerUnsupportedVersionException
import common.validation.validateToken
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.VersionsInterface
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.parseVersionNumber
import ocpi.versions.repositories.VersionsRepository

class VersionsValidationService(
    private val repository: VersionsRepository,
    private val platformRepository: PlatformRepository
) : VersionsInterface {

    override fun getVersions(
        token: String
    ): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        validateToken(platformRepository = platformRepository, token = token)
        repository.getVersions()
    }

    override fun getVersionDetails(
        token: String,
        versionNumber: String
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                validateToken(platformRepository = platformRepository, token = token)
                repository.getVersionDetails(parsedVersionNumber)
            }
            ?: throw OcpiServerUnsupportedVersionException("Invalid version number: $versionNumber")
    }
}