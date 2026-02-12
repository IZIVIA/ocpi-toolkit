package com.izivia.ocpi.toolkit.modules.credentials.services

import com.izivia.ocpi.toolkit.common.OcpiClientUnknownTokenException
import com.izivia.ocpi.toolkit.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.transport.PartnerProviderInterface

class PartnerProvider(
    private val partnerRepository: PartnerRepository,
) : PartnerProviderInterface<ModuleID, InterfaceRole> {

    override suspend fun getEndpointUrl(
        partnerId: String,
        moduleId: ModuleID,
        role: InterfaceRole,
    ): String {
        return partnerRepository
            .getEndpoints(partnerId = partnerId)
            .find { it.identifier == moduleId && it.role == role }
            ?.url
            ?: throw OcpiToolkitUnknownEndpointException(endpointName = moduleId.name)
    }

    override suspend fun getClientToken(partnerId: String, allowTokenA: Boolean): String {
        return with(partnerRepository) {
            if (allowTokenA) {
                getCredentialsClientToken(partnerId = partnerId)
                    ?: getCredentialsTokenA(partnerId = partnerId)
                    ?: throw OcpiClientUnknownTokenException(
                        "Could not find token A or client token associated with partner $partnerId",
                    )
            } else {
                getCredentialsClientToken(partnerId = partnerId)
                    ?: throw OcpiClientUnknownTokenException(
                        "Could not find client token associated with partner $partnerId",
                    )
            }
        }
    }
}
