package com.izivia.ocpi.toolkit211.modules.tariff

import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest
import com.izivia.ocpi.toolkit211.common.TransportClientBuilder
import com.izivia.ocpi.toolkit211.common.parseOptionalResult
import com.izivia.ocpi.toolkit211.common.parseResultOrNull
import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit211.serialization.mapper
import com.izivia.ocpi.toolkit211.serialization.serializeObject

class TariffCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : TariffEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tariffs,
        )

    override suspend fun getTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Tariff? = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.GET,
                path = "/$countryCode/$partyId/$tariffId",
            ),
        ).parseOptionalResult()
    }

    override suspend fun putTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
        tariff: Tariff,
    ): TariffPartial = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.PUT,
                path = "/$countryCode/$partyId/$tariffId",
                body = mapper.serializeObject(tariff),
            ),
        ).parseResultOrNull() ?: TariffPartial()
    }

    override suspend fun deleteTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Unit = with(buildTransport()) {
        send(
            HttpRequest(
                method = HttpMethod.DELETE,
                path = "/$countryCode/$partyId/$tariffId",
            ),
        ).parseResultOrNull<Any>()
    }
}
