package ocpi.versions.validation

import common.OcpiClientInvalidParametersException
import common.OcpiResponseBody
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
        if (isValidToken(token)) {
            repository.getVersions()
        } else {
            throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN ($token)")
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
                    throw OcpiClientInvalidParametersException("Invalid CREDENTIALS_TOKEN_A ($token)")
                }
            }
            ?: throw OcpiClientInvalidParametersException("Invalid version number: $versionNumber")
    }

    /**
     * TODO: is it the good behaviour given:
     * - tokenA: Valid in receiver context, during sender registration (only for sender -> receiver calls)
     * - tokenB: Valid in sender context, during sender registration (only for receiver -> sender calls)
     * - tokenC: Valid when the sender is registered with the receiver (only for sender -> receiver)
     */
    private fun isValidToken(token: String): Boolean =
        platformRepository.getPlatformByTokenA(token) != null ||
                platformRepository.getPlatformByTokenB(token) != null ||
                platformRepository.getPlatformByTokenC(token) != null
}