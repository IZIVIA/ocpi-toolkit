package ocpi.credentials

import common.OcpiResponseBody
import common.authorizationHeader
import common.mapper
import common.parseBody
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
                    path = "/credentials",
                    headers = mapOf(authorizationHeader(token = tokenC))
                )
            )
            .parseBody()


    override fun post(tokenA: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/credentials",
                    headers = mapOf(authorizationHeader(token = tokenA)),
                    body = mapper.writeValueAsString(credentials)
                )
            )
            .parseBody()

    override fun put(tokenC: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/credentials",
                    headers = mapOf(authorizationHeader(token = tokenC)),
                    body = mapper.writeValueAsString(credentials)
                )
            )
            .parseBody()

    override fun delete(tokenC: String): OcpiResponseBody<Credentials?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.DELETE,
                    path = "/credentials",
                    headers = mapOf(authorizationHeader(token = tokenC))
                )
            )
            .parseBody()
}