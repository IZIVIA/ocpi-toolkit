package com.izivia.ocpi.toolkit.common.context

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
