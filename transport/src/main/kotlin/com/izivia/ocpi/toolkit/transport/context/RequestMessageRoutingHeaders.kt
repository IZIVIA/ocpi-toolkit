package com.izivia.ocpi.toolkit.transport.context

import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders.OCPI_FROM_COUNTRY_CODE
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders.OCPI_FROM_PARTY_ID
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders.OCPI_TO_COUNTRY_CODE
import com.izivia.ocpi.toolkit.transport.domain.HttpHeaders.OCPI_TO_PARTY_ID
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Contains context about the current MessageRoutingHeaders
 */
data class RequestMessageRoutingHeaders(
    val toPartyId: String? = null,
    val toCountryCode: String? = null,
    val fromPartyId: String? = null,
    val fromCountryCode: String? = null,
) : AbstractCoroutineContextElement(RequestMessageRoutingHeaders) {
    companion object Key : CoroutineContext.Key<RequestMessageRoutingHeaders>

    fun toHttpHeaders(): Map<String, String> =
        listOf(
            OCPI_TO_PARTY_ID to toPartyId,
            OCPI_TO_COUNTRY_CODE to toCountryCode,
            OCPI_FROM_PARTY_ID to fromPartyId,
            OCPI_FROM_COUNTRY_CODE to fromCountryCode,
        )
            .mapNotNull { entry -> entry.second?.let { entry.first to it } }
            .toMap()
}

/**
 * Retrieves MessageRoutingHeaders in the current coroutine if it is found.
 */
suspend fun currentRequestMessageRoutingHeadersOrNull(): RequestMessageRoutingHeaders? =
    coroutineContext[RequestMessageRoutingHeaders]

/**
 * Retrieves MessageRoutingHeaders in the current coroutine, and throws IllegalStateException
 * if it could not be found.
 */
suspend fun currentRequestMessageRoutingHeaders(): RequestMessageRoutingHeaders =
    coroutineContext[RequestMessageRoutingHeaders]
        ?: throw IllegalStateException("No MessageRoutingHeaders object in current coroutine context")
