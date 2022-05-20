package ocpi.credentials

import common.OcpiResponseBody
import common.authenticate
import common.mapper
import common.parseBody
import ocpi.credentials.domain.Credentials
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 * @property transportClient TransportClient
 */
class CredentialsClient(
    private val transportClient: TransportClient
): CredentialsInterface {

    override fun get(tokenC: String): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.GET
                ).authenticate(token = tokenC)
            )
            .parseBody()


    override fun post(tokenA: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.writeValueAsString(credentials)
                ).authenticate(token = tokenA)
            )
            .parseBody()

    override fun delete(tokenC: String): OcpiResponseBody<Credentials?> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.DELETE,
                    path = "/"
                ).authenticate(token = tokenC)
            )
            .parseBody()
}