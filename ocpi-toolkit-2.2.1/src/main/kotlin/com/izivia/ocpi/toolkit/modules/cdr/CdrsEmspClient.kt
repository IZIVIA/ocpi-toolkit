package com.izivia.ocpi.toolkit.modules.cdr

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

class CdrsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val serverVersionsEndpointUrl: String,
    private val partnerRepository: PartnerRepository
) : CdrsCpoInterface {
    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.cdrs,
            partnerUrl = serverVersionsEndpointUrl,
            partnerRepository = partnerRepository
        )

    override suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?
    ): OcpiResponseBody<SearchResult<Cdr>> =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    queryParams = listOfNotNull(
                        dateFrom?.let { "date_from" to it.toString() },
                        dateTo?.let { "date_to" to it.toString() },
                        "offset" to offset.toString(),
                        limit?.let { "limit" to it.toString() }
                    ).toMap()
                ).withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId()
                )
                    .authenticate(partnerRepository = partnerRepository, partnerUrl = serverVersionsEndpointUrl)
            )
                .parsePaginatedBody(offset)
        }

    suspend fun getCdrsNextPage(
        previousResponse: OcpiResponseBody<SearchResult<Cdr>>
    ): OcpiResponseBody<SearchResult<Cdr>>? = getNextPage(
        transportClientBuilder = transportClientBuilder,
        serverVersionsEndpointUrl = serverVersionsEndpointUrl,
        partnerRepository = partnerRepository,
        previousResponse = previousResponse
    )
}
