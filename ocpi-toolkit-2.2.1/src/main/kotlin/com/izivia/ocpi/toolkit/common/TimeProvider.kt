package com.izivia.ocpi.toolkit.common

import java.time.Instant

fun interface TimeProvider {
    fun now(): Instant
}
