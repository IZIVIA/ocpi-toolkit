package ocpi.versions

import common.httpResponse
import common.tokenFilter
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.validation.VersionsValidationService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod

class VersionsServer(
    transportServer: TransportServer,
    platformRepository: PlatformRepository,
    validationService: VersionsValidationService,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/versions")
    )
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath,
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                validationService.getVersions()
            }
        }
    }
}