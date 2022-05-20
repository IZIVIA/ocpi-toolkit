package com.izivia.ocpi.toolkit.common.validation

import com.izivia.ocpi.toolkit.common.CiString
import org.valiktor.Constraint
import org.valiktor.ConstraintViolationException
import org.valiktor.Validator
import org.valiktor.i18n.toMessage
import java.net.URL
import java.util.*

fun ConstraintViolationException.toReadableString(): String = constraintViolations
    .map { it.toMessage(baseName = "messages", locale = Locale.ENGLISH) }
    .joinToString(",") { "${it.property}: ${it.message}" }

class PrintableAsciiConstraint : Constraint
class PrintableUtf8Constraint : Constraint
class MaxLengthContraint(val length: Int) : Constraint
class CountryCodeConstraint : Constraint
class UrlConstraint : Constraint
class TimeZoneConstraint : Constraint
class LatitudeConstraint : Constraint
class LongitudeConstraint : Constraint
class LanguageConstraint : Constraint
class TimeConstraint : Constraint
class EvseIdConstraint : Constraint
class NoHtmlConstraint : Constraint
class RegularHoursSetWhenNotTwentyFourSevenConstraint : Constraint
class RegularHoursSetAtTheSameTimeAsTwentyFourSevenConstraint : Constraint

fun String.isPrintableAscii(): Boolean = matches("[\\x20-\\x7E]*".toRegex())
fun String.isPrintableUtf8(): Boolean = matches("[\\x20-\\x7E]*".toRegex())

/**
 * Valid if the given string only has printable ASCII characters and is smaller or has the same length as the given one.
 */
fun <E> Validator<E>.Property<String?>.isPrintableUtf8() =
    this.validate(PrintableUtf8Constraint()) {
        it == null || it.isPrintableUtf8()
    }

/**
 * Valid if the given string only has printable UTF-8 characters and is smaller or has the same length as the given one.
 */
fun <E> Validator<E>.Property<String?>.isPrintableAscii() =
    this.validate(PrintableAsciiConstraint()) {
        it == null || it.isPrintableAscii()
    }

fun <E> Validator<E>.Property<String?>.hasNoHtml() =
    this.validate(NoHtmlConstraint()) {
        it == null || !it.matches(".*<(\"[^\"]*\"|'[^']*'|[^'\">])*>.*".toRegex())
    }

/**
 * Valid if the string has a max length of the given one
 */
fun <E> Validator<E>.Property<String?>.hasMaxLengthOf(length: Int) =
    this.validate(MaxLengthContraint(length)) { it == null || it.length <= length }

/**
 * Valid if the string correspond is a ISO 3166-1 alpha-2 or alpha-3 code
 */
fun <E> Validator<E>.Property<String?>.isCountryCode(caseSensitive: Boolean, alpha2: Boolean) =
    this.validate(CountryCodeConstraint()) {
        it == null || Locale
            .getISOCountries(if (alpha2) Locale.IsoCountryCode.PART1_ALPHA2 else Locale.IsoCountryCode.PART1_ALPHA3)
            .contains(
                if (caseSensitive) it else it.uppercase(Locale.ENGLISH)
            )
    }

/**
 * Valid if the string is an URL following the w3.org spec
 */
fun <E> Validator<E>.Property<String?>.isUrl() =
    this.validate(UrlConstraint()) {
        it == null || try { URL(it).toURI().let { true } } catch (e: Exception) { false }
    }.hasMaxLengthOf(255)


/**
 * Valid if the string is one of IANA tzdata's TZ-values representing the time zone.
 * Examples: "Europe/Oslo", "Europe/Zurich". (http://www.iana.org/time-zones)
 */
fun <E> Validator<E>.Property<String?>.isTimeZone() =
    this.validate(TimeZoneConstraint()) {
        it == null || TimeZone.getAvailableIDs().contains(it)
    }

/**
 * Valid if the string is a latitude in decimal degree. Regex: -?[0-9]{1,2}\.[0-9]{6}
 */
fun <E> Validator<E>.Property<String?>.isLatitude() =
    this.validate(LatitudeConstraint()) {
        it == null || it.matches("-?\\d{1,2}\\.\\d{5,7}".toRegex())
    }

/**
 * Valid if the string is a longitude in decimal degree. Regex: -?[0-9]{1,3}\.[0-9]{6}
 */
fun <E> Validator<E>.Property<String?>.isLongitude() =
    this.validate(LongitudeConstraint()) {
        it == null || it.matches("-?\\d{1,3}\\.\\d{5,7}".toRegex())
    }

/**
 * Valid if the string is a language Code ISO 639-1
 */
fun <E> Validator<E>.Property<String?>.isLanguage() =
    this.validate(LanguageConstraint()) {
        it == null || Iso639Alpha2.values().map { code -> code.name }.contains(it)
    }.hasMaxLengthOf(2)

/**
 * Valid if the string is a time from 00:00 to 23:59
 */
fun <E> Validator<E>.Property<String?>.isTime() =
    this.validate(TimeConstraint()) {
        it == null || it.matches("([0-1]\\d|2[0-3]):[0-5]\\d".toRegex())
    }

/**
 * Compliant with the following specification for EVSE ID from "eMI3 standard version V1.0"
 * (http://emi3group.com/documents-links/) "Part 2: business objects." Optional because: if an EVSE ID is to be re-used
 * the EVSE ID can be removed from an EVSE that is removed (status: REMOVED)
 *
 * Extracted from the doc:
 * - {EVSE ID} = {Country Code} {S} {Spot Operator ID} {S} {ID Type} {Power Outlet ID}
 * - Example: FR*A23*E45B*78C
 *
 * With:
 * - Country Code: two character country code according to ISO-3166-1 (Alpha-2-Code). Country Code SHALL represent the
 * country where the EVSE is installed.
 * - Spot Operator ID: three alphanumeric characters, defined and listed by eMI3 group, referring to the EVSE operator
 * - ID Type: one character “E” indicating that this ID represents an “EVSE”
 * - Power Outlet ID: between 1 and 31 sequence of alphanumeric characters or separators, including additional optional
 * separators start with alphanumeric character, internal number allowing the EVSE Operator to identify one specific
 * EVSE
 * - S: optional separator
 *
 * An example for, a valid EVSE ID is “FR*A23*E45B*78C” with “FR” indicating France, “A23” representing a particular
 * EVSE Operator, “E” indicating that it is of type “EVSE” and “45B*78C” representing the power outlet ID, that is to
 * say one of its EVSEs.NOTE: In contrast to the eMA ID, no check digit is specified for the EVSE ID in this document.
 * Alpha characters SHALL be interpreted case insensitively.
 */
fun <E> Validator<E>.Property<CiString?>.isEvseId() =
    this.validate(EvseIdConstraint()) {
        it == null || it.matches("(?i)[a-z]{2}\\*?[a-z\\d]{3}\\*?E[a-z\\d*]{1,31}".toRegex())
    }