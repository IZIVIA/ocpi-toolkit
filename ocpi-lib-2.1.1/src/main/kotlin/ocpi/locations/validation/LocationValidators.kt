package ocpi.locations.validation

import ocpi.locations.domain.*
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.constraints.NotNull
import org.valiktor.constraints.Null
import org.valiktor.functions.isGreaterThanOrEqualTo
import org.valiktor.functions.isLessThanOrEqualTo
import org.valiktor.functions.isNotEmpty
import org.valiktor.validate
import java.math.BigDecimal

fun LocationPartial.validate(): LocationPartial = validate(this) {
    validate(LocationPartial::id).isPrintableAscii().hasMaxLengthOf(39)
    validate(LocationPartial::name).isPrintableAscii().hasMaxLengthOf(255)
    validate(LocationPartial::address).isPrintableAscii().hasMaxLengthOf(45)
    validate(LocationPartial::city).isPrintableAscii().hasMaxLengthOf(45)
    validate(LocationPartial::postal_code).isPrintableAscii().hasMaxLengthOf(10)
    validate(LocationPartial::country).isCountryCode()
    coordinates?.validate()
    related_locations?.forEach { it.validate() }
    evses?.forEach { it.validate() }
    directions?.forEach { it.validate() }
    operator?.validate()
    suboperator?.validate()
    owner?.validate()
    // facilities: nothing to validate
    validate(LocationPartial::time_zone).isTimeZone()
    opening_times?.validate()
    // charging_when_closed: nothing to validate
    images?.forEach { it.validate() }
    energy_mix?.validate()
    // last_updated: nothing to validate
}

fun EnvironmentalImpactPartial.validate(): EnvironmentalImpactPartial = validate(this) {
    // source: nothing to validate
    validate(EnvironmentalImpactPartial::amount)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
}

fun EnergySourcePartial.validate(): EnergySourcePartial = validate(this) {
    // source: nothing to validate
    validate(EnergySourcePartial::percentage)
        .isGreaterThanOrEqualTo(BigDecimal.valueOf(0))
        .isLessThanOrEqualTo(BigDecimal.valueOf(100))
}

fun EnergyMixPartial.validate(): EnergyMixPartial = validate(this) {
    // is_green_energy: nothing to validate
    energy_sources?.forEach { it.validate() }
    environ_impact?.forEach { it.validate() }
    validate(EnergyMixPartial::supplier_name).isPrintableAscii().hasMaxLengthOf(64)
    validate(EnergyMixPartial::energy_product_name).isPrintableAscii().hasMaxLengthOf(64)
}

fun ExceptionalPeriodPartial.validate(): ExceptionalPeriodPartial = validate(this) {
    if (it.period_end != null) {
        validate(ExceptionalPeriodPartial::period_begin).isLessThanOrEqualTo(it.period_end)
    }
}

fun RegularHoursPartial.validate(): RegularHoursPartial = validate(this) { regularHours ->
    validate(RegularHoursPartial::weekday).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(7)
    validate(RegularHoursPartial::period_begin).isTime()
    validate(RegularHoursPartial::period_end).isTime()

    val beginInMinutes = regularHours.period_begin?.split(":")
        ?.mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        ?.sum()
    val endInMinutes = regularHours.period_end?.split(":")
        ?.mapIndexed { index, time -> time.toInt() * (1 - index) * 60 }
        ?.sum()

    if (beginInMinutes != null && endInMinutes != null && beginInMinutes > endInMinutes) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "period_begin ($period_begin) is after period_end ($period_end)",
                constraint = Greater(endInMinutes)
            )
        )
    }
}

fun HoursPartial.validate(): HoursPartial = validate(this) { hours ->
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
    exceptional_openings?.forEach { it.validate() }
    exceptional_closings?.forEach { it.validate() }
}

fun ImagePartial.validate(): ImagePartial = validate(this) {
    validate(ImagePartial::url).isUrl()
    validate(ImagePartial::thumbnail).isUrl()
    // category: nothing to validate
    validate(ImagePartial::type).isPrintableAscii().hasMaxLengthOf(4)
    validate(ImagePartial::width).isLessThanOrEqualTo(99999)
    validate(ImagePartial::height).isLessThanOrEqualTo(99999)
}

fun BusinessDetailsPartial.validate(): BusinessDetailsPartial = validate(this) {
    validate(BusinessDetailsPartial::name).isPrintableAscii().hasMaxLengthOf(100)
    validate(BusinessDetailsPartial::website).isUrl()
    logo?.validate()
}

fun GeoLocationPartial.validate(): GeoLocationPartial = validate(this) {
    validate(GeoLocationPartial::latitude).isLatitude()
    validate(GeoLocationPartial::longitude).isLongitude()
}

fun AdditionalGeoLocationPartial.validate(): AdditionalGeoLocationPartial = validate(this) {
    validate(AdditionalGeoLocationPartial::latitude).isLatitude()
    validate(AdditionalGeoLocationPartial::longitude).isLongitude()
    name?.validate()
}

fun DisplayTextPartial.validate(): DisplayTextPartial = validate(this) {
    validate(DisplayTextPartial::language).isLanguage()
    validate(DisplayTextPartial::text)
        .isPrintableAscii()
        .hasNoHtml()
        .hasMaxLengthOf(512)
}

fun StatusSchedulePartial.validate(): StatusSchedulePartial = validate(this) {
    if (it.period_end != null) {
        validate(StatusSchedulePartial::period_begin).isLessThanOrEqualTo(it.period_end)
    }
}

fun EvsePartial.validate(): EvsePartial = validate(this) {
    validate(EvsePartial::uid).isPrintableAscii().hasMaxLengthOf(39)
    validate(EvsePartial::evse_id).isEvseId()
    // status: nothing to validate
    status_schedule?.forEach { it.validate() }
    // capabilities: nothing to validate
    validate(EvsePartial::connectors).isNotEmpty()
    connectors?.forEach { it.validate() }
    validate(EvsePartial::floor_level).isPrintableAscii().hasMaxLengthOf(4)
    coordinates?.validate()
    validate(EvsePartial::physical_reference).isPrintableAscii().hasMaxLengthOf(16)
    directions?.forEach { it.validate() }
    // parking_restrictions: nothing to validate
    images?.forEach { it.validate() }
    // last_updated: nothing to validate
}

fun ConnectorPartial.validate(): ConnectorPartial = validate(this) {
    validate(ConnectorPartial::id).isPrintableAscii().hasMaxLengthOf(36)
    // standard: nothing to validate
    // format: nothing to validate
    // power_type: nothing to validate
    validate(ConnectorPartial::voltage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::amperage).isGreaterThanOrEqualTo(0)
    validate(ConnectorPartial::tariff_id).isPrintableAscii().hasMaxLengthOf(36)
    validate(ConnectorPartial::terms_and_conditions).isUrl()
    // last_updated: nothing to validate
}

fun Location.validate(): Location = validate(this) {
    toPartial().validate()
}
fun EnvironmentalImpact.validate(): EnvironmentalImpact = validate(this) {
    toPartial().validate()
}
fun EnergySource.validate(): EnergySource = validate(this) {
    toPartial().validate()
}
fun EnergyMix.validate(): EnergyMix = validate(this) {
    toPartial().validate()
}
fun ExceptionalPeriod.validate(): ExceptionalPeriod = validate(this) {
    toPartial().validate()
}
fun RegularHours.validate(): RegularHours = validate(this) {
    toPartial().validate()
}
fun Hours.validate(): Hours = validate(this) { hours ->
     if (hours.regular_hours == null && hours.twenty_four_seven == null) {
        constraintViolations.add(
            DefaultConstraintViolation(
                property = "regular_hours or twenty_four_seven must be set (both are null)",
                constraint = Null
            )
        )
    }

    toPartial().validate()
}
fun Image.validate(): Image = validate(this) {
    toPartial().validate()
}
fun BusinessDetails.validate(): BusinessDetails = validate(this) {
    toPartial().validate()
}
fun GeoLocation.validate(): GeoLocation = validate(this) {
    toPartial().validate()
}
fun AdditionalGeoLocation.validate(): AdditionalGeoLocation = validate(this) {
    toPartial().validate()
}
fun DisplayText.validate(): DisplayText = validate(this) {
    toPartial().validate()
}
fun StatusSchedule.validate(): StatusSchedule = validate(this) {
    toPartial().validate()
}
fun Evse.validate(): Evse = validate(this) {
    toPartial().validate()
}
fun Connector.validate(): Connector = validate(this) {
    toPartial().validate()
}