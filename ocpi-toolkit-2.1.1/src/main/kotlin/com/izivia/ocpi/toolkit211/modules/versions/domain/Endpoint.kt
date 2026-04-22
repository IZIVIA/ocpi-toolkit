package com.izivia.ocpi.toolkit211.modules.versions.domain

/**
 * @property identifier Endpoint identifier
 * @property url URL to the endpoint
 */
data class Endpoint(
    val identifier: ModuleID,
    val url: String,
)
