package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionDetailsValidationService
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