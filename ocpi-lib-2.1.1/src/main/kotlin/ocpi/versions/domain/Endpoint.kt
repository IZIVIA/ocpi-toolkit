package ocpi.versions.domain

/**
 * NOTE: for the credentials module, the role is not relevant as this module is the same for all roles
 *
 * @property identifier Endpoint identifie
 * @property url URL to the endpoint
 */
data class Endpoint(
    val identifier: ModuleID,
    val url: String
)
