package com.izivia.ocpi.toolkit.modules

import com.deblock.jsondiff.DiffGenerator
import com.deblock.jsondiff.matcher.*
import com.deblock.jsondiff.viewer.OnlyErrorDiffViewer
import com.deblock.jsondiff.viewer.PatchDiffViewer
import com.izivia.ocpi.toolkit.common.SearchResult
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
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

fun DescribeableBuilder<String>.isJsonEqualTo(str: String) {
    assertJsonEquals(str, this.subject)
}

fun Assertion.Builder<String>.isJsonEqualTo(str: String) {
    assertJsonEquals(str, this.subject)
}

private fun assertJsonEquals(expected: String, actual: String) {
    val diff = DiffGenerator.diff(
        expected,
        actual,
        CompositeJsonMatcher(
            StrictJsonArrayPartialMatcher(),
            StrictJsonObjectPartialMatcher(),
            LenientNumberPrimitivePartialMatcher(StrictPrimitivePartialMatcher()), // 10.0 == 10.00
        ),
    )

    if (diff.similarityRate() != 100.0) {
        val errorMessage = OnlyErrorDiffViewer.from(diff).toString()
        throw AssertionError(
            "JSON documents differ, ${diff.similarityRate().toInt()}% similarity rate:\n" +
                errorMessage + "\nDiff:\n${PatchDiffViewer.from(diff)}\nExpected:\n$expected\nActual:\n$actual",
        )
    }
}

fun <E> List<E>.toSearchResult(limit: Int = 50, offset: Int = 0) =
    SearchResult(this.subList(offset, Math.min(offset + limit, size)), size, limit, offset, null)
