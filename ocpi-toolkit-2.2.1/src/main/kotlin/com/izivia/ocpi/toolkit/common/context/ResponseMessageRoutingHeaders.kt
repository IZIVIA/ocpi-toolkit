package com.izivia.ocpi.toolkit.common.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Contains context about the current MessageRoutingHeaders
 */
data class ResponseMessageRoutingHeaders(
    var toPartyId: String? = null,
    var toCountryCode: String? = null,
    var fromPartyId: String? = null,
    var fromCountryCode: String? = null
) : AbstractCoroutineContextElement(ResponseMessageRoutingHeaders) {
    companion object Key : CoroutineContext.Key<ResponseMessageRoutingHeaders>
}

/**
 * Retrieves MessageRoutingHeaders in the current coroutine if it is found.
 */
suspend fun currentResponseMessageRoutingHeadersOrNull(): ResponseMessageRoutingHeaders? =
    coroutineContext[ResponseMessageRoutingHeaders]

/**
 * Retrieves MessageRoutingHeaders in the current coroutine, and throws IllegalStateException
 * if it could not be found.
 */
suspend fun currentResponseMessageRoutingHeaders(): ResponseMessageRoutingHeaders =
    coroutineContext[ResponseMessageRoutingHeaders]
        ?: throw IllegalStateException("No MessageRoutingHeaders object in current coroutine context")
