package com.izivia.ocpi.toolkit.modules.tariff.repositories

import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff

interface TariffEmspRepository {
    suspend fun getTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
    ): Tariff?

    suspend fun putTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
        tariff: Tariff,
    ): Tariff

    suspend fun deleteTariff(
        countryCode: CiString,
        partyId: CiString,
        tariffId: CiString,
    ): Tariff?
}
