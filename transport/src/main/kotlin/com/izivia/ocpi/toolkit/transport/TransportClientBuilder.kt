package com.izivia.ocpi.toolkit.transport

interface BaseTransportClientBuilder<M, R> {
    suspend fun buildFor(partnerId: String, module: M, role: R, allowTokenA: Boolean = false): TransportClient
    suspend fun buildFor(partnerId: String, baseUrl: String, allowTokenA: Boolean = false): TransportClient
    suspend fun build(baseUrl: String, authToken: String): TransportClient
}

abstract class AbstractTransportClientBuilder<M, R>(
    private val partnerProvider: PartnerProviderInterface<M, R>,
) : BaseTransportClientBuilder<M, R> {
    override suspend fun buildFor(partnerId: String, module: M, role: R, allowTokenA: Boolean): TransportClient {
        return buildFor(
            partnerId = partnerId,
            baseUrl = partnerProvider.getEndpointUrl(partnerId, module, role),
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
