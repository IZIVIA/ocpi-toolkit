package com.izivia.ocpi.toolkit211.modules.credentials.services

import com.izivia.ocpi.toolkit211.common.OcpiServerNoMatchingEndpointsException
import com.izivia.ocpi.toolkit211.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID

fun checkRequiredEndpoints(
    requiredEndpoints: List<ModuleID>?,
    actualEndpoints: List<Endpoint>,
) {
    if (requiredEndpoints == null) return

    actualEndpoints
        .find { it.identifier == ModuleID.credentials }
        ?: throw OcpiServerNoMatchingEndpointsException("${ModuleID.credentials} endpoint missing")

    for (required in requiredEndpoints) {
        if (required == ModuleID.credentials) continue
        actualEndpoints
            .find { it.identifier == required }
            ?: throw OcpiServerNoMatchingEndpointsException("${required.name} endpoint missing")
    }
}
