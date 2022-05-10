package ocpi.versions

import common.httpResponse
import common.tokenFilter
import ocpi.credentials.repositories.PlatformRepository
import ocpi.versions.validation.VersionsValidationService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.VariablePathSegment

class VersionsServer(
    transportServer: TransportServer,
    platformRepository: PlatformRepository,
    validationService: VersionsValidationService
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/")
            ),
            filters = listOf(platformRepository::tokenFilter)
        ) { req ->
            req.httpResponse {
                validationService.getVersions()
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
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