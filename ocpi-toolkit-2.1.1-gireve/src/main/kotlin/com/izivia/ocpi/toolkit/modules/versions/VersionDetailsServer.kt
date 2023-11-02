package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.VariablePathSegment
import kotlinx.coroutines.runBlocking

class VersionDetailsServer(
    transportServer: TransportServer,
    platformRepository: PlatformRepository,
    validationService: VersionDetailsValidationService,
    basePath: List<FixedPathSegment> = emptyList()
) {
    init {
        runBlocking {
            transportServer.handle(
                method = HttpMethod.GET,
                path = basePath + listOf(
                    VariablePathSegment("versionNumber")
                ),
                filters = listOf(platformRepository::checkToken)
            ) { req ->
                req.httpResponse {
                    validationService.getVersionDetails(
                        versionNumber = req.pathParams["versionNumber"]!!
                    )
                }
            }
        }
    }
}
