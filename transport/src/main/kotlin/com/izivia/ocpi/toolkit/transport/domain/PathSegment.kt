package com.izivia.ocpi.toolkit.transport.domain

sealed interface PathSegment {
    val path: String
}

data class FixedPathSegment(
    override val path: String,
) : PathSegment

data class VariablePathSegment(
    override val path: String,
) : PathSegment
