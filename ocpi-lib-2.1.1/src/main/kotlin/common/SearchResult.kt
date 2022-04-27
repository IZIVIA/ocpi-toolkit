package common

data class SearchResult<T>(
    val list: List<T>,
    val totalCount: Int,
    val limit: Int,
    val offset: Int
)