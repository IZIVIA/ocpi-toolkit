package common

import com.fasterxml.jackson.module.kotlin.readValue
import transport.domain.HttpException
import transport.domain.HttpResponse

inline fun <reified T> HttpResponse.parseBody(): T =
    if (status == 200 || status == 201) {
        mapper.readValue(body!!)
    } else {
        throw HttpException(status)
    }

inline fun <reified T> HttpResponse.parsePaginatedBody(offset: Int): OcpiResponseBody<SearchResult<T>> =
    parseBody<OcpiResponseBody<List<T>>>()
        .let { parsedBody ->
            OcpiResponseBody(
                data = parsedBody.data?.toSearchResult(
                    totalCount = headers["X-Total-Count"]!!.toInt(),
                    limit = headers["X-Limit"]!!.toInt(),
                    offset = offset
                ),
                status_code = parsedBody.status_code,
                status_message = parsedBody.status_message,
                timestamp = parsedBody.timestamp
            )
        }