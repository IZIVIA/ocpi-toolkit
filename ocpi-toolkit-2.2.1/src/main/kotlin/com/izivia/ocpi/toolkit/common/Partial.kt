package com.izivia.ocpi.toolkit.common

interface Partial<T> {
    fun toOcpiDomain(): T
}
