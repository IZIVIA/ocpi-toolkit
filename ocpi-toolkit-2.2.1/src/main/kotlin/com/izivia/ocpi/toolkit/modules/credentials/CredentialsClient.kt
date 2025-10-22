package com.izivia.ocpi.toolkit.modules.credentials

import com.izivia.ocpi.toolkit.common.*
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
                HttpRequest(method = HttpMethod.GET)
                    .withRequiredHeaders(
                        requestId = transportClient.generateRequestId(),
                        correlationId = transportClient.generateCorrelationId(),
                    )
                    .authenticate(token = token),
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
                )
                    .withRequiredHeaders(
                        requestId = transportClient.generateRequestId(),
                        correlationId = transportClient.generateCorrelationId(),
                    )
                    .authenticate(token = token),
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
                )
                    .withRequiredHeaders(
                        requestId = transportClient.generateRequestId(),
                        correlationId = transportClient.generateCorrelationId(),
                    )
                    .authenticate(token = token),
            )
            .parseResult()

    override suspend fun delete(token: String) {
        transportClient
            .send(
                HttpRequest(method = HttpMethod.DELETE)
                    .withRequiredHeaders(
                        requestId = transportClient.generateRequestId(),
                        correlationId = transportClient.generateCorrelationId(),
                    )
                    .authenticate(token = token),
            )
            .parseResultOrNull<String>()
    }
}
