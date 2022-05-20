package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.tokenFilter
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
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