package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.common.authenticate
import com.izivia.ocpi.toolkit.common.mapper
import com.izivia.ocpi.toolkit.common.parseBody
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import transport.TransportClient
import transport.domain.HttpMethod
import transport.domain.HttpRequest

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 * @property transportClient TransportClient
 */
class CredentialsClient(
    private val transportClient: TransportClient
): com.izivia.ocpi.toolkit.modules.credentials.CredentialsInterface {

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