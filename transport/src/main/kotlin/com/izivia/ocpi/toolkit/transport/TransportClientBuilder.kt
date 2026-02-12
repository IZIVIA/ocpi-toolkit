package com.izivia.ocpi.toolkit.transport

interface BaseTransportClientBuilder<M, R> {
    suspend fun buildFor(partnerId: String, module: M, role: R): TransportClient
    fun build(baseUrl: String): TransportClient
}

abstract class AbstractTransportClientBuilder<M, R>(
    protected val partnerProvider: PartnerProviderInterface<M, R>,
) : BaseTransportClientBuilder<M, R> {
    override suspend fun buildFor(partnerId: String, module: M, role: R): TransportClient {
        return build(partnerProvider.getEndpointUrl(partnerId, module, role))
    }
}
