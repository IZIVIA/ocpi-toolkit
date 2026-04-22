package com.izivia.ocpi.toolkit211.common

import java.time.Instant

fun interface TimeProvider {
    fun now(): Instant
}
