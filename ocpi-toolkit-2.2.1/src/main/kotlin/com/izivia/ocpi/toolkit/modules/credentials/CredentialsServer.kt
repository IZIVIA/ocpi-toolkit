package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod

class CredentialsServer(
    private val service: CredentialsServerService,
    basePath: String = "/2.2.1/credentials"
) : OcpiModuleServer(basePath) {
    override suspend fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments,
            secured = false
        ) { req ->
            req.httpResponse {
                service.get(
                    tokenC = req.parseAuthorizationHeader()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments,
            secured = false
        ) { req ->
            req.httpResponse {
                service.post(
                    tokenA = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java),
                    debugHeaders = req.getDebugHeaders()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments,
            secured = false
        ) { req ->
            req.httpResponse {
                service.put(
                    tokenC = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java),
                    debugHeaders = req.getDebugHeaders()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments,
            secured = false
        ) { req ->
            req.httpResponse {
                service.delete(
                    tokenC = req.parseAuthorizationHeader()
                )
            }
        }
    }
}
