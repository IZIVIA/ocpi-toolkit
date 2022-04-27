package common

data class  SearchResult<T>(
    val list: List<T>,
    val totalCount: Int,
    val limit: Int,
    val offset: Int
)

fun <T> List<T>.toSearchResult(totalCount: Int, limit: Int, offset: Int) = SearchResult(
    list = this,
    totalCount = totalCount,
    limit = limit,
    offset = offset
)