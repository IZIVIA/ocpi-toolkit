package com.izivia.ocpi.toolkit.modules.tariff.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.locations.services.validate
import com.izivia.ocpi.toolkit.modules.tariff.domain.PriceComponentPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffElementPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffRestrictionsPartial
import org.valiktor.validate

fun TariffPartial.validate(): TariffPartial = validate(this) {
    validate(TariffPartial::countryCode).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(TariffPartial::partyId).isPrintableAscii().hasMaxLengthOf(3)
    validate(TariffPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    validate(TariffPartial::currency).isCurrencyCode(false).hasMaxLengthOf(3)
    // type: nothing to validate
    tariffAltText?.forEach { displayText -> displayText.validate() }
    validate(TariffPartial::tariffAltUrl).isUrl()
    minPrice?.validate()
    maxPrice?.validate()
    elements?.forEach { element -> element.validate() }
    // startDateTime nothing to validate
    // endDateTime nothing to validate
    energyMix?.validate()
    // lastUpdated: nothing to validate
}

private fun TariffElementPartial.validate() = validate(this) {
    priceComponents?.forEach { priceComponent -> priceComponent.validate() }
    restrictions?.validate()
}

private fun PriceComponentPartial.validate() = validate(this) {
    // type: nothing to validate
    validate(PriceComponentPartial::price).validate()
    validate(PriceComponentPartial::vat).validate()
    validate(PriceComponentPartial::stepSize).isPositive()
}

private fun TariffRestrictionsPartial.validate() = validate(this) {
    validate(TariffRestrictionsPartial::startTime).isTime()
    validate(TariffRestrictionsPartial::endTime).isTime()
    validate(TariffRestrictionsPartial::startDate).isDate()
    validate(TariffRestrictionsPartial::endDate).isDate()
    validate(TariffRestrictionsPartial::minKwh).validate()
    validate(TariffRestrictionsPartial::maxKwh).validate()
    validate(TariffRestrictionsPartial::minCurrent).validate()
    validate(TariffRestrictionsPartial::maxCurrent).validate()
    validate(TariffRestrictionsPartial::minPower).validate()
    validate(TariffRestrictionsPartial::maxPower).validate()
    validate(TariffRestrictionsPartial::minDuration).isPositive()
    validate(TariffRestrictionsPartial::maxDuration).isPositive()
    // dayOfWeek: nothing to validate
    // reservation: nothing to validate
}
