package com.izivia.ocpi.toolkit.modules.versions.domain

/**
 * List of known versions.
 */
enum class VersionNumber(val value: String, val index: Int) {
    /**
     * OCPI version 2.2 (DEPRECATED, do not use, use 2.2.1 instead)
     */
    V2_2("2.2", 3),

    /**
     * OCPI version 2.2.1 (this version)
     */
    V2_2_1("2.2.1", 4),
}

fun parseVersionNumber(value: String): VersionNumber? =
    VersionNumber.values().firstOrNull { it.value == value }
