package com.izivia.ocpi.toolkit211.modules.versions.domain

enum class VersionNumber(val value: String, val index: Int) {
    V2_1_1("2.1.1", 1),
}

fun parseVersionNumber(value: String): VersionNumber? =
    VersionNumber.values().firstOrNull { it.value == value }
