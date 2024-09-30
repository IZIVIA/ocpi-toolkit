package com.izivia.ocpi.toolkit.common.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Contains context about the current token header
 */
data class TokenHeader(
    val token: String? = null,
) : AbstractCoroutineContextElement(TokenHeader) {
    companion object Key : CoroutineContext.Key<TokenHeader>
}

/**
 * Retrieves TokenMessageAuthorizationHeader in the current coroutine if it is found.
 */
suspend fun currentTokenHeaderOrNull(): TokenHeader? =
    coroutineContext[TokenHeader]

/**
 * Retrieves TokenMessageAuthorizationHeader in the current coroutine, and throws IllegalStateException
 * if it could not be found.
 */
suspend fun currentTokenHeader(): TokenHeader =
    coroutineContext[TokenHeader]
        ?: throw IllegalStateException("No TokenHeader object in current coroutine context")
