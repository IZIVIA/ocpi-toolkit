package com.izivia.ocpi.toolkit.common

fun interface Partial<T> {
    fun toOcpiDomain(): T
}
