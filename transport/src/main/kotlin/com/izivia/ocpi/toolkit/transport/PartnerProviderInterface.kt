package com.izivia.ocpi.toolkit.transport

interface PartnerProviderInterface<M, R> {
    suspend fun getEndpointUrl(partnerId: String, moduleId: M, role: R): String
    suspend fun getClientToken(partnerId: String, allowTokenA: Boolean): String
}
