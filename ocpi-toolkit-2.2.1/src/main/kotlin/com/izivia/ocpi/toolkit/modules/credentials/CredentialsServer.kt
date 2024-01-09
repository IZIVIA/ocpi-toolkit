package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.services.CredentialsServerService
import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod

class CredentialsServer(
    private val service: CredentialsServerService,
    basePath: String = "/2.2.1/credentials"
) : OcpiModuleServer(basePath) {
    override suspend fun registerOn(transportServer: TransportServer) {
        transportServer.handle(
            method = HttpMethod.GET,
            path = basePathSegments
        ) { req ->
            req.httpResponse {
                service.get(
                    token = req.parseAuthorizationHeader()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.POST,
            path = basePathSegments
        ) { req ->
            req.httpResponse {
                service.post(
                    token = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java),
                    debugHeaders = req.getDebugHeaders()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.PUT,
            path = basePathSegments
        ) { req ->
            req.httpResponse {
                service.put(
                    token = req.parseAuthorizationHeader(),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java),
                    debugHeaders = req.getDebugHeaders()
                )
            }
        }

        transportServer.handle(
            method = HttpMethod.DELETE,
            path = basePathSegments
        ) { req ->
            req.httpResponse {
                service.delete(
                    token = req.parseAuthorizationHeader()
                )
            }
        }
    }
}
