package com.izivia.ocpi.toolkit.modules.versions.domain

/**
 * List of known versions.
 */
enum class VersionNumber(val value: String, val index: Int) {

    /**
     * OCPI version 2.0
     */
    V2_0("2.0", 0),

    /**
     * OCPI version 2.1 (DEPRECATED, do not use, use 2.1.1 instead)
     */
    V2_1("2.1", 1),

    /**
     * OCPI version 2.1.1
     */
    V2_1_1("2.1.1", 2)
}

fun parseVersionNumber(value: String): VersionNumber? =
    VersionNumber.values().firstOrNull { it.value == value }