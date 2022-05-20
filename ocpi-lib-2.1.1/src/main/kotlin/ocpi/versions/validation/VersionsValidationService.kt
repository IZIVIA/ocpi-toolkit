package ocpi.versions.validation

import common.OcpiResponseBody
import ocpi.versions.VersionsInterface
import ocpi.versions.domain.Version
import ocpi.versions.repositories.VersionsRepository

class VersionsValidationService(
    private val repository: VersionsRepository
) : VersionsInterface {

    override fun getVersions(): OcpiResponseBody<List<Version>> = OcpiResponseBody.of {
        repository.getVersions()
    }
}