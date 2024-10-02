package com.izivia.ocpi.toolkit.modules.types

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * @property language (max-length=2) Language Code ISO 639-1
 * @property text (max-length=512) Text to be displayed to an end user. No markup, html etc. allowed.
 */
@Partial
data class DisplayText(
    val language: String,
    val text: String,
)
