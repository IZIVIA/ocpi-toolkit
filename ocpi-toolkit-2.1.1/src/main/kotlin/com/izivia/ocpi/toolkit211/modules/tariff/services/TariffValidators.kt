package com.izivia.ocpi.toolkit211.modules.tariff.services

import com.izivia.ocpi.toolkit211.common.validation.*
import com.izivia.ocpi.toolkit211.modules.locations.services.validate
import com.izivia.ocpi.toolkit211.modules.tariff.domain.*
import org.valiktor.validate

fun Tariff.validate(): Tariff = validate(this) {
    toPartial().validate()
}

fun TariffPartial.validate(): TariffPartial = validate(this) {
    validate(TariffPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    validate(TariffPartial::currency).isCurrencyCode(false).hasMaxLengthOf(3)
    tariffAltText?.forEach { displayText -> displayText.validate() }
    validate(TariffPartial::tariffAltUrl).isUrl()
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
    validate(TariffRestrictionsPartial::minPower).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::maxPower).isBigDecimalPositive()
    validate(TariffRestrictionsPartial::minDuration).isIntPositive()
    validate(TariffRestrictionsPartial::maxDuration).isIntPositive()
    // dayOfWeek: nothing to validate
}
