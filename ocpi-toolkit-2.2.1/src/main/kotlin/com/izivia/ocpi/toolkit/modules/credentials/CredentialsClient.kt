package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.parseResult
import com.izivia.ocpi.toolkit.common.parseResultOrNull
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 * @property transportClient TransportClient
 */
class CredentialsClient(
    private val transportClient: TransportClient,
) : CredentialsInterface {

    override suspend fun get(token: String): Credentials =
        transportClient
            .send(
                HttpRequest(method = HttpMethod.GET),
            )
            .parseResult()

    override suspend fun post(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.POST,
                    body = mapper.serializeObject(credentials),
                ),
            )
            .parseResult()

    override suspend fun put(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials =
        transportClient
            .send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    body = mapper.serializeObject(credentials),
                ),
            )
            .parseResult()

    override suspend fun delete(token: String) {
        transportClient
            .send(
                HttpRequest(method = HttpMethod.DELETE),
            )
            .parseResultOrNull<String>()
    }
}
