package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.serialization.deserializeOcpiResponse
import com.izivia.ocpi.toolkit.serialization.deserializeOcpiResponseList
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

// the only acceptable HTTP success codes from an OCPI platforms are 200 and 201 according to
// https://github.com/ocpi/ocpi/blob/v2.2.1-d2/status_codes.asciidoc
fun HttpStatus.success() = code == 200 || code == 201

/**
 * Parse body of a paginated request while ignoring invalid entries. Using this method avoids loosing the entire
 * response due to some bad entries. Invalid entries will get logged and filtered out. The result will be stored in a
 * SearchResult which contains all pagination information. Any error information contained in HTTP request or OCPI body
 * gets converted into Exceptions.
 * @param offset
 */
inline fun <reified T, reified P : Partial<T>> HttpResponse.parseSearchResultIgnoringInvalid(
    offset: Int,
    logger: Logger = LogManager.getLogger(HttpResponse::class.java),
): SearchResult<T> {
    return parseSearchResult<P>(offset).let { result ->
        val filtered = result.list.mapNotNull {
            runCatching {
                it.toOcpiDomain()
            }.onFailure { e ->
                logger.warn("invalid object in response", e)
            }.getOrNull()
        }
        SearchResult(filtered, result.totalCount, result.limit, result.offset, result.nextPageUrl)
    }
}

/**
 * Parse body of a paginated request. The result will be stored in a SearchResult which contains all pagination
 * information. Any error information contained in HTTP request or OCPI body gets converted into Exceptions.
 * @param offset
 */
inline fun <reified T> HttpResponse.parseSearchResult(offset: Int): SearchResult<T> =
    parseOcpiResponseBodyAndHandleErrors { mapper.deserializeOcpiResponseList<T>(body) }
        .orEmpty()
        .toSearchResult(
            totalCount = getHeader(Header.X_TOTAL_COUNT)?.toInt()
                ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_TOTAL_COUNT),
            limit = getHeader(Header.X_LIMIT)?.toInt()
                ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_LIMIT),
            offset = offset,
            nextPageUrl = getHeader(Header.LINK)?.split("<")?.elementAtOrNull(1)?.split(">")?.first(),
        )

/**
 * Parse body of a request that should contain data, usually POST commands.
 * Throws an exception if OcpiResponseBody::data is empty
 * Any error information contained in HTTP request or OcpiResponseBody gets converted into Exceptions.
 */
inline fun <reified T> HttpResponse.parseResult(): T {
    return parseResultOrNull() ?: throw OcpiClientGenericException("missing obligatory data in response")
}

inline fun <reified T> HttpResponse.parseResultList(): List<T> {
    return parseResultListOrNull<T>() ?: throw OcpiClientGenericException("missing obligatory data in response")
}

/**
 * Parse body of an object GET request, automatically handling HTTP 404 NOT FOUND errors.
 * Any error information contained in HTTP request or OcpiResponseBody gets converted into Exceptions.
 */
inline fun <reified T> HttpResponse.parseOptionalResult(): T? {
    if (status == HttpStatus.NOT_FOUND) return null
    return parseResultOrNull()
}

inline fun <reified T> HttpResponse.parseResultListOrNull(): List<T>? =
    parseOcpiResponseBodyAndHandleErrors { mapper.deserializeOcpiResponseList<T>(body) }

inline fun <reified T> HttpResponse.parseResultOrNull(): T? =
    parseOcpiResponseBodyAndHandleErrors { mapper.deserializeOcpiResponse<T>(it) }

/**
 * Parse body of a request that might contain data, like PUT/PATCH calls.
 * Any error information contained in HTTP request or OcpiResponseBody gets converted into Exceptions.
 */
inline fun <reified T> HttpResponse.parseOcpiResponseBodyAndHandleErrors(
    deserializeFn: (String?) -> OcpiResponseBody<T>,
): T? {
    if (!status.success()) {
        // We know there was an error, so an exception must be thrown, we will try to throw an OcpiException if the
        // error is formatted as an OCPI error
        runCatching { deserializeFn(body) }
            .getOrElse {
                // If deserialization fails, it means that the response is probably not an OCPI error (if it is, it is
                // incorrectly formatted, so we read it as a regular HttpException)
                throw HttpException(status, status.name)
            }
            .throwOcpiException(status)
    }

    // We know the message is a success, so it must be formatted as an OCPI response, so we can safely throw an
    // exception if it is not the case
    return runCatching { deserializeFn(body) }
        .getOrElse { e ->
            throw OcpiToolkitResponseParsingException("Response cannot be parsed: $body", e)
        }
        .also { parsedOcpiResponse ->
            // who knows, maybe the partner responded with 200 or 201, but the OCPI payload is an error, in that case
            // throw the error
            if (!parsedOcpiResponse.statusCode.toOcpiStatus().isSuccess()) {
                parsedOcpiResponse.throwOcpiException(status)
            }
        }
        .data
}

inline fun <reified T> OcpiResponseBody<T>.throwOcpiException(httpStatus: HttpStatus) {
    throw OcpiException(
        httpStatus = httpStatus,
        ocpiStatus = statusCode.toOcpiStatus(),
        ocpiStatusCode = statusCode,
        message = statusMessage.orEmpty(),
    )
}
