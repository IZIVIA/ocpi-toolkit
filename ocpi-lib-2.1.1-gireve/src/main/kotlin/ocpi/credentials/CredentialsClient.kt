package ocpi.credentials

import common.*
import ocpi.credentials.domain.Credentials
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class CredentialsClient(
    private val transportClient: TransportClient
): CredentialsInterface {

    override fun get(tokenC: String): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/credentials"
                ).authenticate(token = tokenC)
            )
            .parseBody()


    override fun post(tokenA: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/credentials",
                    body = mapper.writeValueAsString(credentials)
                ).authenticate(token = tokenA)
            )
            .parseBody()

    override fun delete(tokenC: String): OcpiResponseBody<Credentials?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.DELETE,
                    path = "/credentials"
                ).authenticate(token = tokenC)
            )
            .parseBody()
}