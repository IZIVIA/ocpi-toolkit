package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * @property name (max-length=100) Name of the operator.
 * @property website Link (string(255) type following the w3.org spec. to the operator's website.
 * @property logo Image link to the operator's logo.
 * @constructor
 */
@Partial
data class BusinessDetails(
    val name: String,
    val website: String? = null,
    val logo: Image? = null,
)
