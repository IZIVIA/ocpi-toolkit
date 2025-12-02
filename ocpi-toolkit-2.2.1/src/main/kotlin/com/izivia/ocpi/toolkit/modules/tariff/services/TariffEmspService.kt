package com.izivia.ocpi.toolkit.modules.tariff.services

import com.izivia.ocpi.toolkit.modules.tariff.TariffEmspInterface
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.toPartial
import com.izivia.ocpi.toolkit.modules.tariff.repositories.TariffEmspRepository

class TariffEmspService(
    private val repository: TariffEmspRepository,
) : TariffEmspInterface {

    override suspend fun getTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Tariff? =
        repository.getTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId)

    override suspend fun putTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
        tariff: Tariff,
    ): TariffPartial {
        return repository.putTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId, tariff = tariff)
            .toPartial()
    }

    override suspend fun deleteTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ) {
        repository.deleteTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId)
    }
}
