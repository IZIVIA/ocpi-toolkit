package com.izivia.ocpi.toolkit.modules.versions.domain

/**
 * NOTE: for the credentials module, the role is not relevant as this module is the same for all roles
 *
 * @property identifier Endpoint identifie
 * @property role Interface role this endpoint implements
 * @property url URL to the endpoint
 */
data class Endpoint(
    val identifier: ModuleID,
    val role: InterfaceRole,
    val url: String,
)
