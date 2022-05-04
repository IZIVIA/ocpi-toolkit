package ocpi.credentials

import common.OcpiResponseBody
import common.mapper
import common.parseBody
import ocpi.credentials.domain.Credentials
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

class CredentialsClient(
    private val transportClient: TransportClient
): CredentialsInterface {

    override fun get() {
        TODO("Not yet implemented")
    }

    override fun post(tokenA: String, credentials: Credentials): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    path = "/credentials",
                    headers = mapOf(
                        "Authorization" to "Token $tokenA"
                    ),
                    body = mapper.writeValueAsString(credentials)
                )
            )
            .parseBody()

    override fun put() {
        TODO("Not yet implemented")
    }

    override fun delete() {
        TODO("Not yet implemented")
    }
}