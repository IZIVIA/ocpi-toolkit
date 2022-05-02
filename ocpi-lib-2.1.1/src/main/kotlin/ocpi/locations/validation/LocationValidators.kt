package ocpi.locations.validation

import ocpi.locations.domain.*
import ocpi.locations.validators.*
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.constraints.NotNull
import org.valiktor.functions.*
import org.valiktor.validate
import java.math.BigDecimal

fun Location.validate(): Location = validate(this) {
    validate(Location::id).isValid { it.length <= 39 }
    validate(Location::name).isValid { it.length <= 255 }
    validate(Location::address).isValid { it.length <= 45 }
    validate(Location::city).isValid { it.length <= 45 }
    validate(Location::postal_code).isValid { it.length <= 10 }
    validate(Location::country).isValid { it.isValidCountry() }
    coordinates.validate()
    related_locations.forEach { it.validate() }
    evses.forEach { it.validate() }
    directions.forEach { it.validate() }
    operator?.validate()
    suboperator?.validate()
    owner?.validate()
    // facilities: nothing to validate
    validate(Location::time_zone).isValid { it.isValidTimeZone() }
    opening_times?.validate()
    // charging_when_closed: nothing to validate
    images.forEach { it.validate() }
    energy_mix?.validate()
    // last_updated: nothing to validate
}

fun EnvironmentalImpact.validate(): EnvironmentalImpact = validate(this) {
    // source: nothing to validate
    validate(EnvironmentalImpact::amount)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
}

fun EnergySource.validate(): EnergySource = validate(this) {
    // source: nothing to validate
    validate(EnergySource::percentage)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
        .isLessThanOrEqualTo(BigDecimal.valueOf(100))
}

fun EnergyMix.validate(): EnergyMix = validate(this) {
    // is_green_energy: nothing to validate
    energy_sources.forEach { it.validate() }
    environ_impact.forEach { it.validate() }
    validate(EnergyMix::supplier_name).isValid { it.length <= 64 }
    validate(EnergyMix::energy_product_name).isValid { it.length <= 64  }
}

fun ExceptionalPeriod.validate(): ExceptionalPeriod = validate(this) {
    validate(ExceptionalPeriod::period_begin).isLessThan(it.period_end)
}

fun RegularHours.validate(): RegularHours = validate(this) { regularHours ->
    validate(RegularHours::weekday).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(7)
    validate(RegularHours::period_begin).isValid { it.isValidTime() }
    validate(RegularHours::period_end).isValid { it.isValidTime() }

    val beginInMinutes = regularHours.period_begin.split(":")
        .mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        .sum()
    val endInMinutes = regularHours.period_end.split(":")
        .mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        .sum()

    if (beginInMinutes > endInMinutes) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "period_begin ($period_begin) is after period_end ($period_end)",
                constraint = Greater(endInMinutes)
            )
        )
    }
}

fun Hours.validate(): Hours = validate(this) { hours ->
    if (hours.regular_hours != null && hours.twenty_four_seven != null) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "regular_hours and twenty_four_seven are both set (only one must be set)",
                constraint = NotNull
            )
        )
    }

    regular_hours?.forEach { it.validate() }
    /// twenty_four_seven: nothing to validate
    exceptional_openings.forEach { it.validate() }
    exceptional_closings.forEach { it.validate() }
}

fun Image.validate(): Image = validate(this) {
    validate(Image::url).isValid { it.isValidUrl() }
    validate(Image::thumbnail).isValid { it.isValidUrl() }
    // category: nothing to validate
    validate(Image::type).isValid { it.length <= 4 }
    validate(Image::width).isLessThanOrEqualTo(99999)
    validate(Image::height).isLessThanOrEqualTo(99999)
}

fun BusinessDetails.validate(): BusinessDetails = validate(this) {
    validate(BusinessDetails::name).isValid { it.length <= 100 }
    validate(BusinessDetails::website).isValid { it.isValidUrl() }
    logo?.validate()
}

fun GeoLocation.validate(): GeoLocation = validate(this) {
    validate(GeoLocation::latitude).isValid { it.isValidLatitude() }
    validate(GeoLocation::longitude).isValid { it.isValidLongitude() }
}

fun AdditionalGeoLocation.validate(): AdditionalGeoLocation = validate(this) {
    validate(AdditionalGeoLocation::latitude).isValid { it.isValidLatitude() }
    validate(AdditionalGeoLocation::longitude).isValid { it.isValidLongitude() }
    name?.validate()
}

fun DisplayText.validate(): DisplayText = validate(this) {
    validate(DisplayText::language).isValid { it.isValidLanguage() }
    validate(DisplayText::text).isValid { it.length <= 512 }
    validate(DisplayText::text).isValid { it.isRawString() }
}

fun StatusSchedule.validate(): StatusSchedule = validate(this) {
    if (it.period_end != null) {
        validate(StatusSchedule::period_begin).isLessThanOrEqualTo(it.period_end)
    }
}

fun Evse.validate(): Evse = validate(this) {
    validate(Evse::uid).isValid { it.length <= 39 }
    validate(Evse::evse_id).isValid { it.isValidEvseId()  }
    // status: nothing to validate
    status_schedule.forEach { it.validate() }
    // capabilities: nothing to validate
    connectors.forEach { it.validate() }
    validate(Evse::floor_level).isValid { it.length <= 4 }
    coordinates?.validate()
    validate(Evse::physical_reference).isValid { it.length <= 16 }
    directions.forEach { it.validate() }
    // parking_restrictions: nothing to validate
    images.forEach { it.validate() }
    // last_updated: nothing to validate
}

fun Connector.validate(): Connector = validate(this) {
    validate(Connector::id).validate { it.length <= 36 }
    // standard: nothing to validate
    // format: nothing to validate
    // power_type: nothing to validate
    validate(Connector::voltage).isGreaterThanOrEqualTo(0)
    validate(Connector::amperage).isGreaterThanOrEqualTo(0)
    validate(Connector::tariff_id).validate { it.length <= 36 }
    validate(Connector::terms_and_conditions).validate { it.isValidUrl() }
    // last_updated: nothing to validate
}

fun LocationPartial.validate(): LocationPartial = validate(this) {
    // TODO
}

fun EvsePartial.validate(): EvsePartial = validate(this) {
    // TODO
}

fun ConnectorPartial.validate(): ConnectorPartial = validate(this) {
    // TODO
}