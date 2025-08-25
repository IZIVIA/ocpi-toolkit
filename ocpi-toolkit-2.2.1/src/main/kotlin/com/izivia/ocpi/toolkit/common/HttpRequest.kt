package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.common.context.currentResponseMessageRoutingHeadersOrNull
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import org.apache.logging.log4j.LogManager
import java.time.Instant

private val logger = LogManager.getLogger(HttpRequest::class.java)

suspend fun <T> HttpRequest.respondSearchResult(now: Instant, fn: suspend () -> SearchResult<T>) =
    defaultHeadersOrErrorHandling(now) {
        val result = fn()

        HttpResponse(
            status = HttpStatus.OK,
            body = mapper.writeValueAsString(
                OcpiResponseBody(
                    data = result.list,
                    statusCode = OcpiStatus.SUCCESS.code,
                    statusMessage = "Success",
                    timestamp = now,
                ),
            ),
            headers = paginationHeaders(result, this),
        )
    }

private fun paginationHeaders(result: SearchResult<*>, request: HttpRequest): Map<String, String> {
    val nextPageOffset = (result.offset + result.limit).takeIf { it <= result.totalCount }

    val queries = request
        .queryParams
        .filter { it.key != "offset" && it.value != null }
        .plus("offset" to (result.limit + result.offset))
        .map { "${it.key}=${it.value}" }
        .joinToString("&", "?")

    return listOfNotNull(
        nextPageOffset?.let { "Link" to "<${request.baseUrl}${request.path}$queries>; rel=\"next\"" },
        "X-Total-Count" to result.totalCount.toString(),
        "X-Limit" to result.limit.toString(),
    ).toMap()
}

suspend fun <T> HttpRequest.respondObject(now: Instant, fn: suspend () -> T?) =
    respondNullableObject(now) { fn() ?: throw OcpiObjectNotFoundException() }

suspend fun HttpRequest.respondNothing(now: Instant, fn: suspend () -> Unit) =
    respondNullableObject(now) {
        fn()
        null
    }

private suspend fun <T> HttpRequest.respondNullableObject(now: Instant, fn: suspend () -> T?) =
    defaultHeadersOrErrorHandling(now) {
        val result = fn()

        // TODO we are supposed to respond with a 201 CREATED if this is a newly added object
        //      https://github.com/ocpi/ocpi/blob/v2.2.1-d2/status_codes.asciidoc
        //      https://github.com/IZIVIA/ocpi-toolkit/issues/65
        HttpResponse(
            status = HttpStatus.OK,
            body = mapper.writeValueAsString(
                OcpiResponseBody(
                    data = result,
                    statusCode = OcpiStatus.SUCCESS.code,
                    statusMessage = "Success",
                    timestamp = now,
                ),
            ),
        )
    }

private suspend fun HttpRequest.defaultHeadersOrErrorHandling(
    now: Instant,
    fn: suspend () -> HttpResponse,
): HttpResponse {
    val baseHeaders = buildMap {
        put(Header.CONTENT_TYPE, ContentType.APPLICATION_JSON)
        putAll(getDebugHeaders())
        putAll(currentResponseMessageRoutingHeadersOrNull()?.httpHeaders().orEmpty())
    }

    return try {
        fn()
    } catch (e: OcpiException) {
        logger.warn(e)
        e.toHttpResponse(now)
    } catch (e: HttpException) {
        HttpResponse(status = e.status)
    } catch (e: Exception) {
        // at this point, we should only encounter well-defined OcpiExceptions, or unhandled server errors
        // all other issues, like auth and json deserialization should have happened before
        logger.error(e)
        OcpiServerGenericException("Generic server error").toHttpResponse(now)
    }.withHeadersMixin(baseHeaders)
}

/**
 * Transforms an OcpiException to an HttpResponse. May be used in TransportServer implementation to handle
 * OCPI exceptions.
 */
fun OcpiException.toHttpResponse(now: Instant): HttpResponse =
    HttpResponse(
        status = httpStatus,
        body = mapper.writeValueAsString(
            OcpiResponseBody(
                data = null,
                statusCode = ocpiStatusCode,
                statusMessage = message,
                timestamp = now,
            ),
        ),
        headers = if (httpStatus == HttpStatus.UNAUTHORIZED) mapOf("WWW-Authenticate" to "Token") else emptyMap(),
    )
