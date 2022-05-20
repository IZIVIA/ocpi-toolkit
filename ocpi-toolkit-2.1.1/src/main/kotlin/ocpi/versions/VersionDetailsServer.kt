package ocpi.versions

import common.httpResponse
import common.tokenFilter
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.validation.VersionDetailsValidationService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.VariablePathSegment

class VersionDetailsServer(
    transportServer: TransportServer,
    platformRepository: PlatformRepository,
    validationService: VersionDetailsValidationService,
    basePath: List<FixedPathSegment> = emptyList()
) {
    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePath + listOf(
                VariablePathSegment("versionNumber")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                validationService.getVersionDetails(
                    versionNumber = req.pathParams["versionNumber"]!!
                )
            }
        }
    }
}