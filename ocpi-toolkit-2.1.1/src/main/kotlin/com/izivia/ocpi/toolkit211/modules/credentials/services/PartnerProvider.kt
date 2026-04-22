package com.izivia.ocpi.toolkit211.modules.credentials.services

import com.izivia.ocpi.toolkit211.common.OcpiClientUnknownTokenException
import com.izivia.ocpi.toolkit211.common.OcpiToolkitUnknownEndpointException
import com.izivia.ocpi.toolkit211.common.PartnerProviderInterface
import com.izivia.ocpi.toolkit211.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID

class PartnerProvider(
    private val partnerRepository: PartnerRepository,
) : PartnerProviderInterface {

    override suspend fun getEndpointUrl(
        partnerId: String,
        moduleId: ModuleID,
    ): String {
        return partnerRepository
            .getEndpoints(partnerId = partnerId)
            .find { it.identifier == moduleId }
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
