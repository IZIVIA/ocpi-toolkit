package ocpi.versions

import common.toHttpResponse
import ocpi.versions.validation.VersionsValidationService
import transport.TransportServer
import transport.domain.*

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

            validationService
                .getVersions(
                    token = req.headers["Authorization"]
                        ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing")
                )
                .toHttpResponse()
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                VariablePathSegment("versionNumber")
            )
        ) { req ->
            validationService
                .getVersionDetails(
                    token = req.headers["Authorization"]
                        ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing"),
                    versionNumber = req.pathParams["versionNumber"]!!
                )
                .toHttpResponse()
        }
    }
}