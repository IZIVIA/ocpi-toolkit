package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.izivia.ocpi.toolkit.transport.domain.HttpException
import com.izivia.ocpi.toolkit.transport.domain.HttpResponse
import com.izivia.ocpi.toolkit.transport.domain.HttpStatus


// the only acceptable HTTP success codes from an OCPI platforms are 200 and 201 according to
// https://github.com/ocpi/ocpi/blob/v2.2.1-d2/status_codes.asciidoc
fun HttpStatus.success() = code == 200 || code == 201

/**
 * Parse body of a paginated request. The result will be stored in a SearchResult which contains all pagination
 * information.
 * @param offset
 */
inline fun <reified T> HttpResponse.parseSearchResult(offset: Int): SearchResult<T> {
    if (!status.success()) throw HttpException(status, status.name)
    if (body.isNullOrBlank()) throw OcpiClientGenericException("missing obligatory body in response")

    val list = mapper.readValue(body, jacksonTypeRef<OcpiResponseBody<List<T>>>())
        .also {
            if (it.statusCode != OcpiStatus.SUCCESS.code) throw OcpiResponseException(
                it.statusCode,
                it.statusMessage ?: "",
            )
        }
        .data ?: emptyList()

    return list.toSearchResult(
        totalCount = getHeader(Header.X_TOTAL_COUNT)?.toInt()
            ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_TOTAL_COUNT),
        limit = getHeader(Header.X_LIMIT)?.toInt()
            ?: throw OcpiToolkitMissingRequiredResponseHeaderException(Header.X_LIMIT),
        offset = offset,
        nextPageUrl = getHeader(Header.LINK)?.split("<")?.elementAtOrNull(1)?.split(">")?.first(),
    )
}

/**
 * Parse body of a regular request.
 */
inline fun <reified T> HttpResponse.parseResult(): T? {
    if (!status.success()) throw HttpException(status, status.name)
    if (body.isNullOrBlank()) throw OcpiClientGenericException("missing obligatory body in response")

    return mapper.readValue(body, jacksonTypeRef<OcpiResponseBody<T>>())
        .also {
            if (it.statusCode != OcpiStatus.SUCCESS.code) throw OcpiResponseException(
                it.statusCode,
                it.statusMessage ?: "",
            )
        }
        .data
}
