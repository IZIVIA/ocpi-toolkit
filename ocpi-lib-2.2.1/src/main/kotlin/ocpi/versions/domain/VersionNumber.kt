package ocpi.versions.domain

/**
 * List of known versions.
 */
enum class VersionNumber(val value: String, val index: Int) {
    /**
     * OCPI version 2.1.1
     */
    V2_1_1("2.1.1", 2),
}

fun parseVersionNumber(value: String): VersionNumber? =
    VersionNumber.values().firstOrNull { it.value == value }