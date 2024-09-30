package com.izivia.ocpi.toolkit.common

data class SearchResult<T>(
    val list: List<T>,
    val totalCount: Int,
    val limit: Int,
    val offset: Int,
    val nextPageUrl: String?,
)

fun <T> List<T>.toSearchResult(totalCount: Int, limit: Int, offset: Int, nextPageUrl: String? = null) = SearchResult(
    list = this,
    totalCount = totalCount,
    limit = limit,
    offset = offset,
    nextPageUrl = nextPageUrl,
)
