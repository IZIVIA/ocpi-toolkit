package com.izivia.ocpi.toolkit.modules

import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import io.json.compare.CompareMode
import io.json.compare.JSONCompare
import org.http4k.core.Uri
import org.http4k.core.queries
import strikt.api.Assertion
import strikt.api.DescribeableBuilder

fun buildHttpRequest(httpMethod: HttpMethod, path: String, body: String? = null): HttpRequest = Uri.of(path).let {
    HttpRequest(
        method = httpMethod,
        path = it.path,
        queryParams = it.queries().toMap(),
        body = body,
    )
}

fun DescribeableBuilder<String>.isJsonEqualTo(
    str: String,
    vararg compareModes: CompareMode = arrayOf(
        CompareMode.REGEX_DISABLED,
        CompareMode.JSON_OBJECT_NON_EXTENSIBLE,
        CompareMode.JSON_ARRAY_NON_EXTENSIBLE,
    ),
) {
    assertJsonEquals(str, this.subject, compareModes.toSet())
}

fun Assertion.Builder<String>.isJsonEqualTo(
    str: String,
    vararg compareModes: CompareMode = arrayOf(
        CompareMode.REGEX_DISABLED,
        CompareMode.JSON_OBJECT_NON_EXTENSIBLE,
        CompareMode.JSON_ARRAY_NON_EXTENSIBLE,
    ),
) {
    assertJsonEquals(str, this.subject, compareModes.toSet())
}

private fun assertJsonEquals(
    expected: String,
    actual: String,
    compareModes: Set<CompareMode>,
) =
    JSONCompare.assertMatches(expected, actual, compareModes)

fun <E> List<E>.toSearchResult(limit: Int = 50, offset: Int = 0) =
    SearchResult(this.subList(offset, Math.min(offset + limit, size)), size, limit, offset, null)
