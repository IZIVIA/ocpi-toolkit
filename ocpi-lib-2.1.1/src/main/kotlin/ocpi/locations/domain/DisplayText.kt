package ocpi.locations.domain

/**
 * @property language (max-length=2) Language Code ISO 639-1
 * @property text (max-length=512) Text to be displayed to an end user. No markup, html etc. allowed.
 */
data class DisplayText(
    val language: String,
    val text: String
)

data class DisplayTextPatch(
    val language: String?,
    val text: String?
)
