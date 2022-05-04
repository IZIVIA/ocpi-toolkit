package ocpi.versions

import common.decodeBase64
import common.httpResponse
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
        ) { req -> httpResponse {
            validationService
                .getVersions(
                    token = req.headers["Authorization"]
                        ?.removePrefix("Token ")
                        ?.decodeBase64()
                        ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing")
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.GET,
            path = listOf(
                VariablePathSegment("versionNumber")
            )
        ) { req ->
            httpResponse {
                validationService
                    .getVersionDetails(
                        token = req.headers["Authorization"]
                            ?.removePrefix("Token ")
                            ?.decodeBase64()
                            ?: throw HttpException(HttpStatus.UNAUTHORIZED, "Authorization header missing"),
                        versionNumber = req.pathParams["versionNumber"]!!
                    )
            }
        }
    }
}