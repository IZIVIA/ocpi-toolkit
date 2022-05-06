package ocpi.versions

import common.httpResponse
import common.parseAuthorizationHeader
import ocpi.versions.validation.VersionsValidationService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod
import transport.domain.VariablePathSegment

class VersionsServer(
    transportServer: TransportServer,
    validationService: VersionsValidationService
) {

    init {
        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                FixedPathSegment("/")
            )
        ) { req ->
            req.httpResponse {
                validationService.getVersions(
                    token = req.parseAuthorizationHeader()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                VariablePathSegment("versionNumber")
            )
        ) { req ->
            req.httpResponse {
                validationService.getVersionDetails(
                    token = req.parseAuthorizationHeader(),
                    versionNumber = req.pathParams["versionNumber"]!!
                )
            }
        }
    }
}