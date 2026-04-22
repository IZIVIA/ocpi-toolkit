package com.izivia.ocpi.toolkit211.modules.tariff.repositories

import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff

interface TariffEmspRepository {

    suspend fun getTariff(countryCode: String, partyId: String, tariffId: String): Tariff?

    suspend fun putTariff(countryCode: String, partyId: String, tariffId: String, tariff: Tariff): Tariff

    suspend fun deleteTariff(countryCode: String, partyId: String, tariffId: String)
}
