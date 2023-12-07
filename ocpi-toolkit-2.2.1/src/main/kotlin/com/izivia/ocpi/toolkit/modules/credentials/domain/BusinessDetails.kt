package com.izivia.ocpi.toolkit.modules.credentials.domain

/**
 * @property name (max-length=100) Name of the operator.
 * @property website URL to the operator's website.
 * @property logo Image link to the operator's logo.
 */
data class BusinessDetails(
    val name: String,
    val website: String?,
    val logo: Image?
)
