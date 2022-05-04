package ocpi.credentials

import common.OcpiClientNotEnoughInformationException
import common.decodeBase64
import common.httpResponse
import common.mapper
import ocpi.credentials.domain.Credentials
import ocpi.credentials.services.CredentialsServerService
import transport.TransportServer
import transport.domain.FixedPathSegment
import transport.domain.HttpMethod

class CredentialsServer(
    transportServer: TransportServer,
    service: CredentialsServerService
) {
    init {
        // TODO: get

        transportServer.handle(
            method = HttpMethod.POST,
            path = listOf(
                FixedPathSegment("/credentials")
            )
        ) { req -> httpResponse {
                service.post(
                    tokenA = req.headers["Authorization"]
                        ?.removePrefix("Token ")
                        ?.decodeBase64()
                        ?: throw OcpiClientNotEnoughInformationException("Missing Authorization header"),
                    credentials = mapper.readValue(req.body!!, Credentials::class.java)
                )
            }
        }

        // TODO: put
        // TODO: delete
    }
}