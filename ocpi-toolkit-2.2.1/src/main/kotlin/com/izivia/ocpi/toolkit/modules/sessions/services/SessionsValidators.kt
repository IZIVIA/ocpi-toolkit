package com.izivia.ocpi.toolkit.modules.sessions.services

import com.izivia.ocpi.toolkit.common.validation.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.*
import com.izivia.ocpi.toolkit.modules.types.Price
import com.izivia.ocpi.toolkit.modules.types.PricePartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import org.valiktor.functions.isGreaterThan
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.validate
import java.time.Instant

fun SessionPartial.validate(): SessionPartial = validate(this) {
    validate(SessionPartial::country_code).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(SessionPartial::party_id).isPrintableAscii().hasMaxLengthOf(3)
    validate(SessionPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::start_date_time).isGreaterThan(Instant.now())
    validate(SessionPartial::end_date_time).isGreaterThan(it.start_date_time ?: Instant.now())
    validate(SessionPartial::kwh).isGreaterThanOrEqualTo(0)
    cdr_token?.validate()
    //auth_method: nothing to validate
    validate(SessionPartial::authorization_reference).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::location_id).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::evse_uid).isEvseId()
    validate(SessionPartial::connector_id).isPrintableAscii().hasMaxLengthOf(36)
    validate(SessionPartial::meter_id).isPrintableAscii().hasMaxLengthOf(255)
    validate(SessionPartial::currency).isCurrencyCode(false)
    charging_periods?.forEach() { charging_period -> charging_period.validate() }
    total_cost?.validate()
    //status: nothing to validate
    //last_updated: nothing to validate
}

fun ChargingPreferencesPartial.validate(): ChargingPreferencesPartial = validate(this) {
    //profile_type nothing to validate
    validate(ChargingPreferencesPartial::departure_time).isGreaterThan(Instant.now())
    validate(ChargingPreferencesPartial::energy_need).isGreaterThanOrEqualTo(0)
    //discharge_allowed nothing to validate
}

fun CdrTokenPartial.validate(): CdrTokenPartial = validate(this) {
    validate(CdrTokenPartial::country_code).isCountryCode(caseSensitive = false, alpha2 = true)
    validate(CdrTokenPartial::party_id).isPrintableAscii().hasMaxLengthOf(3)
    validate(CdrTokenPartial::uid).isPrintableAscii().hasMaxLengthOf(36)
    //tokenType : nothing to validate
    validate(CdrTokenPartial::contract_id).isPrintableAscii().hasMaxLengthOf(36)
}

fun ChargingPeriodPartial.validate(): ChargingPeriodPartial = validate(this) {
    validate(ChargingPeriodPartial::start_date_time).isGreaterThan(Instant.now())
    dimensions?.forEach() { dimension -> dimension.validate() }
    validate(ChargingPeriodPartial::tariff_id).isPrintableAscii().hasMaxLengthOf(36)
}

fun CdrDimensionPartial.validate(): CdrDimensionPartial = validate(this) {
    //cdrDimensionType : nothing to validate
    validate(CdrDimensionPartial::volume).isGreaterThanOrEqualTo(0)
}

fun PricePartial.validate(): PricePartial = validate(this) {
    validate(PricePartial::excl_vat).isGreaterThanOrEqualTo(0)
    validate(PricePartial::incl_vat).isGreaterThanOrEqualTo(0)
}

fun Session.validate(): Session = validate(this) {
    toPartial().validate()
}

fun ChargingPreferences.validate(): ChargingPreferences = validate(this) {
    toPartial().validate()
}

fun CdrToken.validate(): CdrToken = validate(this) {
    toPartial().validate()
}

fun ChargingPeriod.validate(): ChargingPeriod = validate(this) {
    toPartial().validate()
}

fun CdrDimension.validate(): CdrDimension = validate(this) {
    toPartial().validate()
}

fun Price.validate(): Price = validate(this) {
    toPartial().validate()
}
