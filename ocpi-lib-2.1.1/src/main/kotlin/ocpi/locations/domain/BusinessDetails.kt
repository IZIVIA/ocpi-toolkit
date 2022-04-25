package ocpi.locations.domain

/**
 * @property name Name of the operator.
 * @property website Link (string(255) type following the w3.org spec.) to the operator's website.
 * @property logo Image link to the operator's logo.
 * @constructor
 */
data class BusinessDetails(
    val name: String,
    val website: String?,
    val logo: Image?
)

data class BusinessDetailsPatch(
    val name: String?,
    val website: String?,
    val logo: ImagePatch?
)

