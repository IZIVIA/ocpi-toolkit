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

    override fun get(tokenC: String): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(method = HttpMethod.GET)
                    .withDebugHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()


    override fun post(tokenA: String, credentials: Credentials, debugHeaders: Map<String, String>): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.writeValueAsString(credentials)
                )
                    .withDebugHeaders()
                    .authenticate(token = tokenA)
            )
            .parseBody()

    override fun put(tokenC: String, credentials: Credentials, debugHeaders: Map<String, String>): OcpiResponseBody<Credentials> =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    body = mapper.writeValueAsString(credentials)
                )
                    .withDebugHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()

    override fun delete(tokenC: String): OcpiResponseBody<Credentials?> =
        transportClient
            .send(
                HttpRequest(method = HttpMethod.DELETE)
                    .withDebugHeaders()
                    .authenticate(token = tokenC)
            )
            .parseBody()
}