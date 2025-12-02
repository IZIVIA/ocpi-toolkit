package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.common.*
import com.izivia.ocpi.toolkit.modules.credentials.repositories.PartnerRepository
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.TransportClientBuilder
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class TariffCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
    private val partnerRepository: PartnerRepository,
) : TariffEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            module = ModuleID.tariffs,
            partnerId = partnerId,
            partnerRepository = partnerRepository,
        )

    override suspend fun getTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
    ): Tariff? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$tariffId",
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .parseOptionalResult()
    }

    override suspend fun putTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
        tariff: Tariff,
    ): TariffPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$tariffId",
                body = mapper.serializeObject(tariff),
            )
                .withRequiredHeaders(
                    requestId = generateRequestId(),
                    correlationId = generateCorrelationId(),
                )
                .authenticate(partnerRepository = partnerRepository, partnerId = partnerId),
        )
            .parseResultOrNull() ?: TariffPartial()
    }

    override suspend fun deleteTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
    ): Unit = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.DELETE,
                path = "/$countryCode/$partyId/$tariffId",
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
