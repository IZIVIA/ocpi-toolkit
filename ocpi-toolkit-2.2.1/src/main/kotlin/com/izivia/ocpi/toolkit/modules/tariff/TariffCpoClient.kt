package com.izivia.ocpi.toolkit.modules.tariff

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.TransportClientBuilder
import com.izivia.ocpi.toolkit.common.parseOptionalResult
import com.izivia.ocpi.toolkit.common.parseResultOrNull
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.serialization.mapper
import com.izivia.ocpi.toolkit.serialization.serializeObject
import com.izivia.ocpi.toolkit.transport.TransportClient
import com.izivia.ocpi.toolkit.transport.domain.HttpMethod
import com.izivia.ocpi.toolkit.transport.domain.HttpRequest

class TariffCpoClient(
    private val transportClientBuilder: TransportClientBuilder,
    private val partnerId: String,
) : TariffEmspInterface {

    private suspend fun buildTransport(): TransportClient = transportClientBuilder
        .buildFor(
            partnerId = partnerId,
            module = ModuleID.tariffs,
            role = InterfaceRole.RECEIVER,
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
            ),
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
            ),
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
            ),
        )
            .parseResultOrNull<Any>()
    }
}
