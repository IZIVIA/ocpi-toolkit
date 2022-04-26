package transport.domain

data class PathSegment(
    val path: String,
    val param: Boolean = false
)
