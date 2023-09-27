package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.httpResponse
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.common.parseAuthorizationHeader
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import kotlinx.coroutines.runBlocking

class CredentialsServer(
    transportServer: TransportServer,
    service: CredentialsServerService,
    basePath: List<FixedPathSegment> = listOf(
        FixedPathSegment("/2.1.1/credentials")
    )
) {
    init {
        runBlocking {
            transportServer.handle(
                method = HttpMethod.GET,
                path = basePath
            ) { req ->
                req.httpResponse {
                    service.get(
                        tokenC = req.parseAuthorizationHeader()
                    )
                }
            }

            transportServer.handle(
                method = HttpMethod.POST,
                path = basePath
            ) { req ->
                req.httpResponse {
                    service.post(
                        tokenA = req.parseAuthorizationHeader(),
                        credentials = mapper.readValue(req.body!!, Credentials::class.java)
                    )
                }
            }

            transportServer.handle(
                method = HttpMethod.PUT,
                path = basePath
            ) { req ->
                req.httpResponse {
                    service.put(
                        tokenC = req.parseAuthorizationHeader(),
                        credentials = mapper.readValue(req.body!!, Credentials::class.java)
                    )
                }
            }

            transportServer.handle(
                method = HttpMethod.DELETE,
                path = basePath
            ) { req ->
                req.httpResponse {
                    service.delete(
                        tokenC = req.parseAuthorizationHeader()
                    )
                }
            }
        }
    }
}
