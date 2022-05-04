package ocpi.versions.validation

import common.OcpiClientInvalidParametersException
import common.OcpiResponseBody
import ocpi.credentials.repositories.ServerPlatformRepository
import ocpi.versions.VersionsInterface
import ocpi.versions.domain.Version
import ocpi.versions.domain.VersionDetails
import ocpi.versions.domain.parseVersionNumber
import ocpi.versions.repositories.VersionsRepository

class VersionsValidationService(
    private val repository: VersionsRepository,
    private val serverPlatformRepository: ServerPlatformRepository
) : VersionsInterface {

    override fun getVersions(
        token: String
    ): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        val validToken = serverPlatformRepository.getPlatformByTokenA(token) != null ||
                serverPlatformRepository.getPlatformByTokenC(token) != null

        if (validToken) {
            repository.getVersions()
        } else {
            throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A") // TODO: ou C
        }
    }

    override fun getVersionDetails(
        token: String,
        versionNumber: String
    ): OcpiResponseBody<VersionDetails> = OcpiResponseBody.of {
        parseVersionNumber(versionNumber)
            ?.let { parsedVersionNumber ->
                if (isValidToken(token)) {
                    repository.getVersionDetails(parsedVersionNumber)
                } else {
                    throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A")
                }
            }
            ?: throw OcpiClientInvalidParametersException("Invalid version number: $versionNumber")
    }

    private fun isValidToken(token: String): Boolean =
        serverPlatformRepository.getPlatformByTokenA(token) != null ||
                serverPlatformRepository.getPlatformByTokenC(token) != null
}