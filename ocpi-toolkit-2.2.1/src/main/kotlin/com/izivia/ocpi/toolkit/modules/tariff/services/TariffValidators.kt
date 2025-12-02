package com.izivia.ocpi.toolkit.modules.tariff.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.locations.services.validate
import com.izivia.ocpi.toolkit.modules.tariff.domain.*
import org.valiktor.validate

fun Tariff.validate(): Tariff = validate(this) {
    toPartial().validate()
}

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
    validate(PriceComponentPartial::price).isBigDecimalPositive()
    validate(PriceComponentPartial::vat).isBigDecimalPositive()
    validate(PriceComponentPartial::stepSize).isIntPositive()
}

private fun TariffRestrictionsPartial.validate() = validate(this) {
    validate(TariffRestrictionsPartial::startTime).isTime()
    validate(TariffRestrictionsPartial::endTime).isTime()
    validate(TariffRestrictionsPartial::startDate).isDate()
    validate(TariffRestrictionsPartial::endDate).isDate()
    validate(TariffRestrictionsPartial::minKwh).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::maxKwh).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::minCurrent).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::maxCurrent).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::minPower).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::maxPower).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::minDuration).isIntPositive()
    validate(TariffRestrictionsPartial::maxDuration).isIntPositive()
    // dayOfWeek: nothing to validate
    // reservation: nothing to validate
}
