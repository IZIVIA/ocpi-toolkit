package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

suspend inline fun <reified T> getNextPage(
    transportClientBuilder: TransportClientBuilder,
    partnerId: String,
    partnerRepository: PartnerRepository,
    previousResponse: OcpiResponseBody<SearchResult<T>>,
): OcpiResponseBody<SearchResult<T>>? =
    previousResponse.data?.nextPageUrl?.let { nextPageUrl ->
        with(transportClientBuilder.build(nextPageUrl)) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parsePaginatedBody(previousResponse.data.offset)
        }
    }
