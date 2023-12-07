package com.izivia.ocpi.toolkit.common

import java.util.*

fun generateUUIDv4Token(): String {
    return UUID.randomUUID().toString()
}

typealias CiString = String
