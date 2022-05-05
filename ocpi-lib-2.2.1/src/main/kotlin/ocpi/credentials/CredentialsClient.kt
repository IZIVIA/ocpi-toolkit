package ocpi.credentials

import common.OcpiResponseBody
import common.encodeBase64
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
                    method = HttpMethod.POST,
                    path = "/credentials",
                    headers = mapOf(
                        "Authorization" to "Token ${tokenC.encodeBase64()}"
                    )
                )
            )
            .parseBody()


    override fun post(tokenA: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/credentials",
                    headers = mapOf(
                        "Authorization" to "Token ${tokenA.encodeBase64()}"
                    ),
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
                    headers = mapOf(
                        "Authorization" to "Token ${tokenC.encodeBase64()}"
                    ),
                    body = mapper.writeValueAsString(credentials)
                )
            )
            .parseBody()

    override fun delete() {
        TODO("Not yet implemented")
    }
}