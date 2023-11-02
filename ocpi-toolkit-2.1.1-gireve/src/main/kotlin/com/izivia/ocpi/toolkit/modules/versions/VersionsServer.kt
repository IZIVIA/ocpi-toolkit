package com.izivia.ocpi.toolkit.modules.versions

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.checkToken
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PlatformRepository
import com.izivia.ocpi.toolkit.modules.versions.validation.VersionsValidationService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import kotlinx.coroutines.runBlocking

class VersionsServer(
    transportServer: TransportServer,
    platformRepository: PlatformRepository,
    validationService: VersionsValidationService,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/versions")
    )
) {

    init {
        runBlocking {
            transportServer.handle(
                method = HttpMethod.GET,
                path = basePath,
                filters = listOf(platformRepository::checkToken)
            ) { req ->
                req.httpResponse {
                    validationService.getVersions()
                }
            }
        }
    }
}
