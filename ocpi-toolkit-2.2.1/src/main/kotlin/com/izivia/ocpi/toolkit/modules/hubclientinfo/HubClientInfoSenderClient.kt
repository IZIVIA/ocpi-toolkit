package com.izivia.ocpi.toolkit.modules.hubclientinfo

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class HubClientInfoSenderClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : HubClientInfoReceiverInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.hubclientinfo,
            role = InterfaceRole.RECEIVER,
        )

    override suspend fun get(
        countryCode: String,
        partyId: String,
    ): ClientInfo? =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.GET,
                    path = "/$countryCode/$partyId",
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseOptionalResult()
        }

    override suspend fun put(
        countryCode: String,
        partyId: String,
        clientInfo: ClientInfo,
    ): Unit =
        with(buildTransport()) {
            send(
                HttpRequest(
                    method = HttpMethod.PUT,
                    path = "/$countryCode/$partyId",
                    body = mapper.serializeObject(clientInfo),
                )
                    .withRequiredHeaders(
                        requestId = generateRequestId(),
                        correlationId = generateCorrelationId(),
                    )
                    .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
            )
                .parseResultOrNull<Any>()
        }
}
