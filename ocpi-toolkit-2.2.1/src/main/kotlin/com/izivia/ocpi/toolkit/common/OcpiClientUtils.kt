package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

suspend inline fun <reified T, reified P : Partial<T>> getNextPage(
    transportClientBuilder: TransportClientBuilder,
    partnerId: String,
    previousResponse: SearchResult<T>,
    ignoreInvalidListEntry: Boolean = false,
): SearchResult<T>? =
    previousResponse.nextPageUrl?.let { nextPageUrl ->
        with(transportClientBuilder.buildFor(partnerId, nextPageUrl)) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                ),
            ).let { res ->
                if (ignoreInvalidListEntry) {
                    res.parseSearchResultIgnoringInvalid<T, P>(previousResponse.offset)
                } else {
                    res.parseSearchResult<T>(previousResponse.offset)
                }
            }
        }
    }
