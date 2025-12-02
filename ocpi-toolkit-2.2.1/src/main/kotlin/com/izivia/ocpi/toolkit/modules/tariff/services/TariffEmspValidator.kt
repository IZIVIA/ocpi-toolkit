package com.izivia.ocpi.toolkit.modules.tariff.services

import com.izivia.ocpi.toolkit.common.validation.validate
import com.izivia.ocpi.toolkit.common.validation.validateLength
import com.izivia.ocpi.toolkit.common.validation.validateSame
import com.izivia.ocpi.toolkit.modules.tariff.TariffEmspInterface
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial

class TariffEmspValidator(
    private val service: TariffEmspInterface,
) : TariffEmspInterface {

    override suspend fun getTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ): Tariff? {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tariffId", tariffId, 36)
        }

        return service.getTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId)
            ?.validate()
    }

    override suspend fun putTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
        tariff: Tariff,
    ): TariffPartial {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tariffId", tariffId, 36)
            validateSame("countryCode", countryCode, tariff.countryCode)
            validateSame("partyId", partyId, tariff.partyId)
            validateSame("tariffId", tariffId, tariff.id)
            tariff.validate()
        }

        return service.putTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId, tariff = tariff)
            .validate()
    }

    override suspend fun deleteTariff(
        countryCode: String,
        partyId: String,
        tariffId: String,
    ) {
        validate {
            validateLength("countryCode", countryCode, 2)
            validateLength("partyId", partyId, 3)
            validateLength("tariffId", tariffId, 36)
        }
        service.deleteTariff(countryCode = countryCode, partyId = partyId, tariffId = tariffId)
    }
}
