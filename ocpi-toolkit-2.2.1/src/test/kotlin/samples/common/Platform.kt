package samples.common

import ocpi.versions.domain.Endpoint
import ocpi.versions.domain.Version

data class Platform(
    val url: String,
    val version: Version? = null,
    val endpoints: List<Endpoint>? = null,
    val tokenA: String? = null,
    val tokenB: String? = null,
    val tokenC: String? = null
)
