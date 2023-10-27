package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 * @property transportClient TransportClient
 */
class CredentialsClient(
    private val transportClient: TransportClient
): CredentialsInterface {

    override suspend fun get(tokenC: String): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(method = HttpMethod.GET)
                    .withRequiredHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()


    override suspend fun post(tokenA: String, credentials: Credentials, debugHeaders: Map<String, String>): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.writeValueAsString(credentials)
                )
                    .withRequiredHeaders()
                    .authenticate(token = tokenA)
            )
            .parseBody()

    override suspend fun put(tokenC: String, credentials: Credentials, debugHeaders: Map<String, String>): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    body = mapper.writeValueAsString(credentials)
                )
                    .withRequiredHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()

    override suspend fun delete(tokenC: String): OcpiResponseBody<Credentials?> =
        transportClient
            .send(
                HttpRequest(method = HttpMethod.DELETE)
                    .withRequiredHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()
}
