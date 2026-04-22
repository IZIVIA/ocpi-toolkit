package com.izivia.ocpi.toolkit211.modules.credentials

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.parseResult
import com.izivia.ocpi.toolkit211.common.parseResultOrNull
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 */
class CredentialsClient(
    private val transportClient: TransportClient,
) : CredentialsInterface {

    override suspend fun get(token: String): Credentials =
        transportClient
            .send(HttpRequest(method = HttpMethod.GET))
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
            .send(HttpRequest(method = HttpMethod.DELETE))
            .parseResultOrNull<String>()
    }
}
