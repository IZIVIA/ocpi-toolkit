package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class PaginationHeadersTest {

    @Test
    fun `adds a next link when another page is available`() {
        val headers = paginationHeaders(
            result(offset = 0, limit = 1, totalCount = 2),
            request(queryParams = mapOf("limit" to "1", "offset" to "0")),
        )

        expectThat(headers[Header.LINK])
            .isEqualTo("<https://example.com/2.2.1/tokens?limit=1&offset=1>; rel=\"next\"")
    }

    @Test
    fun `does not add a next link after the last full page`() {
        val headers = paginationHeaders(
            result(offset = 1, limit = 1, totalCount = 2),
            request(queryParams = mapOf("limit" to "1", "offset" to "1")),
        )

        expectThat(headers[Header.LINK]).isNull()
    }

    @Test
    fun `does not add a next link after the last partial page`() {
        val headers = paginationHeaders(
            result(offset = 2, limit = 2, totalCount = 3),
            request(queryParams = mapOf("limit" to "2", "offset" to "2")),
        )

        expectThat(headers[Header.LINK]).isNull()
    }

    @Test
    fun `does not add a next link for an empty result`() {
        val headers = paginationHeaders(
            result(offset = 0, limit = 10, totalCount = 0),
            request(queryParams = mapOf("limit" to "10", "offset" to "0")),
        )

        expectThat(headers[Header.LINK]).isNull()
    }

    @Test
    fun `preserves query parameters and replaces the offset in the next link`() {
        val headers = paginationHeaders(
            result(offset = 2, limit = 2, totalCount = 10),
            request(
                queryParams = linkedMapOf(
                    "limit" to "2",
                    "date_from" to "2026-01-01T00:00:00Z",
                    "date_to" to "2026-02-01T00:00:00Z",
                    "filter" to null,
                    "offset" to "2",
                ),
            ),
        )

        expectThat(headers[Header.LINK]).isEqualTo(
            "<https://example.com/2.2.1/tokens?limit=2&date_from=2026-01-01T00:00:00Z" +
                "&date_to=2026-02-01T00:00:00Z&offset=4>; rel=\"next\"",
        )
    }

    private fun result(offset: Int, limit: Int, totalCount: Int) = SearchResult(
        list = emptyList<String>(),
        totalCount = totalCount,
        limit = limit,
        offset = offset,
        nextPageUrl = null,
    )

    private fun request(queryParams: Map<String, String?>) = HttpRequest(
        method = HttpMethod.GET,
        path = "/2.2.1/tokens",
        baseUrl = "https://example.com",
        queryParams = queryParams,
    )
}
