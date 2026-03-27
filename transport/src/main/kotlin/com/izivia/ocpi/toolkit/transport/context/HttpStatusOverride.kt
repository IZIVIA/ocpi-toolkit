package com.izivia.ocpi.toolkit.transport.context

import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Allows service/repository implementations to override the HTTP status code of the response.
 *
 * This is primarily used to distinguish between HTTP 200 (OK) and HTTP 201 (Created) when handling
 * PUT requests, as required by the OCPI specification:
 * - HTTP 200: The object already existed and has successfully been updated.
 * - HTTP 201: The object has been newly created in the server system.
 *
 * @see <a href="https://github.com/ocpi/ocpi/blob/v2.2.1-d2/status_codes.asciidoc">OCPI Status Codes</a>
 */
data class HttpStatusOverride(
    var status: HttpStatus? = null,
) : AbstractCoroutineContextElement(HttpStatusOverride) {
    companion object Key : CoroutineContext.Key<HttpStatusOverride>
}

/**
 * Signals that a new object was created, so the response should use HTTP 201 (Created)
 * instead of the default 200 (OK).
 *
 * Call this from service or repository implementations inside PUT handlers when the object
 * did not previously exist and was newly created.
 *
 * Usage:
 * ```kotlin
 * override suspend fun putLocation(...): Location {
 *     val existing = repository.find(...)
 *     val result = repository.save(location)
 *     if (existing == null) {
 *         signalObjectCreated()
 *     }
 *     return result
 * }
 * ```
 */
suspend fun signalObjectCreated() {
    coroutineContext[HttpStatusOverride]?.status = HttpStatus.CREATED
}

/**
 * Retrieves the HTTP status override from the current coroutine context, if present.
 */
suspend fun currentHttpStatusOverrideOrNull(): HttpStatus? =
    coroutineContext[HttpStatusOverride]?.status
