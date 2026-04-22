package com.izivia.ocpi.toolkit211.modules.cdr

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit211.modules.cdr.domain.CdrPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import java.time.Instant

/**
 * Sends calls to the CPO
 */
class CdrsEmspClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val ignoreInvalidListEntry: Boolean = false,
) : CdrsCpoInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.cdrs,
        )

    override suspend fun getCdrs(
        dateFrom: Instant?,
        dateTo: Instant?,
        offset: Int,
        limit: Int?,
    ): SearchResult<Cdr> = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                queryParams = listOfNotNull(
                    dateFrom?.let { "date_from" to it.toString() },
                    dateTo?.let { "date_to" to it.toString() },
                    "offset" to offset.toString(),
                    limit?.let { "limit" to it.toString() },
                ).toMap(),
            ),
        ).let { res ->
            if (ignoreInvalidListEntry) {
                res.parseSearchResultIgnoringInvalid<Cdr, CdrPartial>(offset)
            } else {
                res.parseSearchResult<Cdr>(offset)
            }
        }
    }

    suspend fun getCdrsNextPage(
        previousResponse: SearchResult<Cdr>,
    ): SearchResult<Cdr>? = getNextPage<Cdr, CdrPartial>(
        transportClientBuilder = transportClientBuilder,
        partnerId = partnerId,
        previousResponse = previousResponse,
        ignoreInvalidListEntry = ignoreInvalidListEntry,
    )
}
