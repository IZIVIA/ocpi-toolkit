package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import java.net.URI
import java.net.URLDecoder
import java.time.Instant

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

    @Test
    fun `encodes a time zone offset so that its plus sign survives the next request`() {
        val headers = paginationHeaders(
            result(offset = 0, limit = 2, totalCount = 10),
            request(queryParams = linkedMapOf("date_from" to "2026-01-01T02:00:00+02:00", "limit" to "2")),
        )

        expectThat(headers[Header.LINK]).isEqualTo(
            "<https://example.com/2.2.1/tokens?date_from=2026-01-01T02:00:00%2B02:00&limit=2&offset=2>; rel=\"next\"",
        )
    }

    @Test
    fun `encodes the characters that would change the meaning of the next link`() {
        val headers = paginationHeaders(
            result(offset = 0, limit = 1, totalCount = 2),
            request(queryParams = linkedMapOf("filter" to "a&b=c #d %e é *~:@/?", "limit" to "1")),
        )

        expectThat(headers[Header.LINK]).isEqualTo(
            "<https://example.com/2.2.1/tokens?filter=a%26b%3Dc%20%23d%20%25e%20%C3%A9%20%2A~:@/?" +
                "&limit=1&offset=1>; rel=\"next\"",
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["2026-01-01T02:00:00+02:00", "2026-01-01T00:00:00Z"])
    fun `following the next link preserves date_from and accepts both offset and UTC dates`(dateFrom: String) {
        val firstRequest = request(queryParams = mapOf("date_from" to dateFrom, "limit" to "2"))
        val headers = paginationHeaders(result(offset = 0, limit = 2, totalCount = 10), firstRequest)

        val nextRequest = requestFromLink(requireNotNull(headers[Header.LINK]))

        expectThat(nextRequest.queryParams["date_from"]).isEqualTo(dateFrom)
        expectThat(nextRequest.queryParamAsInstant("date_from"))
            .isEqualTo(Instant.parse("2026-01-01T00:00:00Z"))
        expectThat(nextRequest.queryParamAsInt("offset")).isEqualTo(2)
    }

    @Test
    fun `an unencoded plus in the next link reproduces OCPI error 2001`() {
        val nextRequest = requestFromLink(
            "<https://example.com/2.2.1/tokens?date_from=2026-01-01T02:00:00+02:00&offset=2>; rel=\"next\"",
        )

        expectThat(nextRequest.queryParams["date_from"]).isEqualTo("2026-01-01T02:00:00 02:00")
        val error = assertThrows<OcpiClientInvalidParametersException> {
            nextRequest.queryParamAsInstant("date_from")
        }
        expectThat(error.ocpiStatusCode).isEqualTo(2001)
    }

    // Simulate following the header URL: query values are decoded before reaching the toolkit.
    private fun requestFromLink(link: String): HttpRequest {
        val uri = URI(link.substringAfter('<').substringBefore('>'))
        val queryParams = uri.rawQuery.split('&').associate { parameter ->
            val (name, value) = parameter.split('=', limit = 2)
            URLDecoder.decode(name, Charsets.UTF_8.name()) to URLDecoder.decode(value, Charsets.UTF_8.name())
        }
        return request(queryParams)
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
