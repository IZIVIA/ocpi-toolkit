package com.izivia.ocpi.toolkit211.modules.tariff

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import java.time.Instant

class TariffEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val ignoreInvalidListEntry: Boolean = false,
) : TariffCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tariffs,
        )

    override suspend fun getTariffs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Tariff> = with(buildTransport()) {
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
        ).let { res ->
            if (ignoreInvalidListEntry) {
                res.parseSearchResultIgnoringInvalid<Tariff, TariffPartial>(offset)
            } else {
                res.parseSearchResult<Tariff>(offset)
            }
        }
    }

    suspend fun getTariffsNextPage(
        previousResponse: SearchResult<Tariff>,
    ): SearchResult<Tariff>? = getNextPage<Tariff, TariffPartial>(
        transportClientBuilder = transportClientBuilder,
        partnerId = partnerId,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )
}
