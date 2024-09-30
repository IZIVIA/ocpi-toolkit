package com.izivia.ocpi.toolkit.modules.versions.domain

/**
 * The Module identifiers for each endpoint are described in the beginning of each Module chapter. The following table
 * contains the list of modules in this version of OCPI. Most modules (except Credentials & Registration) are optional,
 * but there might be dependencies between modules. If there are dependencies between modules, it will be mentioned in
 * the affected module description.
 */
enum class ModuleID {
    cdrs,

    chargingprofiles,

    commands,

    /**
     * Required for all implementations. The role field has no function for this module
     */
    credentials,

    hubclientinfo,

    locations,

    sessions,

    tariffs,

    tokens,

    versions,
}
