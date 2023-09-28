package com.izivia.ocpi.toolkit.common

import com.izivia.ocpi.toolkit.transport.TransportServer
import com.izivia.ocpi.toolkit.transport.domain.FixedPathSegment
import com.izivia.ocpi.toolkit.transport.domain.PathSegment

abstract class OcpiModuleServer(val basePath: String) {
    protected val basePathSegments: List<PathSegment> get() = listOf(FixedPathSegment(basePath))

    abstract suspend fun registerOn(transportServer: TransportServer)
}
