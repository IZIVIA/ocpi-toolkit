package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.parseResult
import com.izivia.ocpi.toolkit.common.parseResultIgnoringData
import com.izivia.ocpi.toolkit.common.withoutMessageRoutingHeaders
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse

/**
 * Use this class only if you know what you are doing. Instead, use CredentialsClientService.
 * @property transportClient TransportClient
 */
class CredentialsClient(
    private val transportClient: TransportClient,
) : CredentialsInterface {

    // Credentials is a configuration module: routing headers SHALL NOT be used
    private suspend fun send(request: HttpRequest): HttpResponse =
        withoutMessageRoutingHeaders { transportClient.send(request) }

    override suspend fun get(token: String): Credentials =
        send(
            HttpRequest(method = HttpMethod.GET),
        )
            .parseResult()

    override suspend fun post(
        token: String,
        credentials: Credentials,
        debugHeaders: Map<String, String>,
    ): Credentials =
        send(
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
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                body = mapper.serializeObject(credentials),
            ),
        )
            .parseResult()

    override suspend fun delete(token: String) {
        send(
            HttpRequest(method = HttpMethod.DELETE),
        )
            .parseResultIgnoringData()
    }
}
