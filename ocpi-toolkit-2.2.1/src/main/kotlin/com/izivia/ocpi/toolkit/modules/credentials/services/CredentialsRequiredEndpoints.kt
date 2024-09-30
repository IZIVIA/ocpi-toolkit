package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.OcpiServerNoMatchingEndpointsException
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID

data class RequiredEndpoints(
    val receiver: List<ModuleID> = listOf(),
    val sender: List<ModuleID> = listOf(),
)

fun checkRequiredEndpoints(
    requiredEndpoints: RequiredEndpoints?,
    actualEndpoints: List<Endpoint>,
) {
    if (requiredEndpoints == null) {
        return
    }

    actualEndpoints
        .find { it.identifier == ModuleID.credentials }
        ?: throw OcpiServerNoMatchingEndpointsException("${ModuleID.credentials} endpoint missing")

    checkRequiredRoleEndpoints(actualEndpoints, requiredEndpoints.receiver, InterfaceRole.RECEIVER)
    checkRequiredRoleEndpoints(actualEndpoints, requiredEndpoints.sender, InterfaceRole.SENDER)
}

private fun checkRequiredRoleEndpoints(
    endpoints: List<Endpoint>,
    requiredEndpoints: List<ModuleID>,
    role: InterfaceRole,
) {
    for (requiredEndpoint in requiredEndpoints) {
        endpoints
            .find {
                requiredEndpoint == ModuleID.credentials ||
                    (it.role == role && it.identifier == requiredEndpoint)
            }
            ?: throw OcpiServerNoMatchingEndpointsException("${requiredEndpoint.name} as ${role.name} endpoint missing")
    }
}
