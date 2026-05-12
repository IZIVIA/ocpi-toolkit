package com.izivia.ocpi.toolkit211.modules.tariff

import com.izivia.ocpi.toolkit211.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit211.modules.tariff.domain.TariffPartial

/**
 * eMSP Interface
 *
 * - GET: Retrieve a Tariff as it is stored in the eMSP's system.
 * - PUT: Push new/updated Tariff object to the eMSP.
 * - DELETE: Remove a Tariff object which is no longer in use.
 */
interface TariffEmspInterface {

    suspend fun getTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Tariff?

    suspend fun putTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
        tariff: Tariff,
    ): TariffPartial

    suspend fun deleteTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    )
}
