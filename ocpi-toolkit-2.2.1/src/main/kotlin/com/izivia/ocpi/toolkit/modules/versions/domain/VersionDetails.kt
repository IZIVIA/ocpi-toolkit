package com.izivia.ocpi.toolkit.modules.versions.domain

/**
 * Via the version details, the parties can exchange which modules are implemented for a specific version of OCPI, which
 * interface role is implemented, and what the endpoint URL is for this interface.
 *
 * Parties that are both CPO and eMSP (or a Hub) can implement one version endpoint that covers both roles. With the
 * information that is available in the version details, parties don’t need to implement a separate endpoint per role
 * (CPO or eMSP) anymore. In practice this means that when a company is both a CPO and an eMSP and it connects to
 * another party that implements both interfaces, only one OCPI connection is needed.
 * @property version The version number.
 * @property endpoints A list of supported endpoints for this version. (at least one in the list)
 */
data class VersionDetails(
    val version: String,
    val endpoints: List<Endpoint>,
)
