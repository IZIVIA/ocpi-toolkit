package ocpi.locations.domain

import io.github.quatresh.annotations.Partial

/**
 * @property name Name of the operator.
 * @property website Link (string(255) type following the w3.org spec.) to the operator's website.
 * @property logo Image link to the operator's logo.
 * @constructor
 */
@Partial
data class BusinessDetails(
    val name: String,
    val website: String?,
    val logo: Image?
)
