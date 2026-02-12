package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseSearchResult
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import java.time.Instant

class HubClientInfoReceiverClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : HubClientInfoSenderInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.hubclientinfo,
            role = InterfaceRole.SENDER,
        )

    override suspend fun getAll(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<ClientInfo> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to dateFrom.toString() },
                    dateTo?.let { "date_to" to dateTo.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to limit.toString() },
                ).toMap(),
            ),
        )
            .parseSearchResult(offset)
    }
}
