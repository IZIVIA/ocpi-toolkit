package com.izivia.ocpi.toolkit211.common

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID

/** Interface for building transport clients for OCPI 2.1.1 */
interface TransportClientBuilder {
    suspend fun buildFor(partnerId: String, module: ModuleID, allowTokenA: Boolean = false): TransportClient
    suspend fun buildFor(partnerId: String, baseUrl: String, allowTokenA: Boolean = false): TransportClient
    suspend fun build(baseUrl: String, authToken: String): TransportClient
}

/** Interface to provide partner endpoint URLs and tokens */
interface PartnerProviderInterface {
    suspend fun getEndpointUrl(partnerId: String, moduleId: ModuleID): String
    suspend fun getClientToken(partnerId: String, allowTokenA: Boolean): String
}

/** Convenience abstract builder that delegates URL/token lookup to a PartnerProviderInterface */
abstract class AbstractTransportClientBuilder(
    private val partnerProvider: PartnerProviderInterface,
) : TransportClientBuilder {
    override suspend fun buildFor(partnerId: String, module: ModuleID, allowTokenA: Boolean): TransportClient {
        return buildFor(
            partnerId = partnerId,
            baseUrl = partnerProvider.getEndpointUrl(partnerId, module),
            allowTokenA = allowTokenA,
        )
    }

    override suspend fun buildFor(partnerId: String, baseUrl: String, allowTokenA: Boolean): TransportClient {
        return build(
            baseUrl = baseUrl,
            authToken = partnerProvider.getClientToken(partnerId, allowTokenA),
        )
    }
}
