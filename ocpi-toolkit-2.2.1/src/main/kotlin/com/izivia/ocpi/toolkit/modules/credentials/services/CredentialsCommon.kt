package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.OcpiServerNoMatchingEndpointsException
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID

class CredentialsCommon {
    companion object {
        fun checkRequiredEndpoints(
            requiredEndpoints: Map<InterfaceRole, List<ModuleID>>,
            actualEndpoints: List<Endpoint>
        ) {
            if (requiredEndpoints.isEmpty()) {
                return
            }

            val requiredOtherPartAsReceiverEndpoints = requiredEndpoints[InterfaceRole.RECEIVER] ?: listOf()
            val requiredOtherPartAsSenderEndpoints = requiredEndpoints[InterfaceRole.SENDER] ?: listOf()

            actualEndpoints
                .find { it.identifier == ModuleID.credentials }
                .let {
                    it ?: throw OcpiServerNoMatchingEndpointsException(
                        "${ModuleID.credentials} other part endpoint missing"
                    )
                }

            checkRequiredRoleEndpoints(actualEndpoints, requiredOtherPartAsReceiverEndpoints, InterfaceRole.RECEIVER)
            checkRequiredRoleEndpoints(actualEndpoints, requiredOtherPartAsSenderEndpoints, InterfaceRole.SENDER)
        }

        private fun checkRequiredRoleEndpoints(
            endpoints: List<Endpoint>,
            requiredEndpoints: List<ModuleID>,
            role: InterfaceRole
        ) {
            for (requiredEndpoint in requiredEndpoints) {
                endpoints
                    .find {
                        requiredEndpoint == ModuleID.credentials ||
                            (it.role == role && it.identifier == requiredEndpoint)
                    }
                    .let {
                        it ?: throw OcpiServerNoMatchingEndpointsException(
                            "${requiredEndpoint.name} as ${role.name} other part endpoint missing"
                        )
                    }
            }
        }
    }
}
