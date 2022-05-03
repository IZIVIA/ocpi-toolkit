package ocpi.locations.validation

import java.net.URL
import java.util.*

/**
 * URL type following the w3.org spec. to the operator's terms and conditions
 */
fun String.isValidUrl(): Boolean =
    try {
        URL(this).toURI()
        length <= 255
    } catch (e: Exception) {
        false
    }


/**
 * One of IANA tzdata's TZ-values representing the time zone of the location.
 * Examples: "Europe/Oslo", "Europe/Zurich". (http://www.iana.org/time-zones)
 */
fun String.isValidTimeZone(): Boolean = TimeZone
    .getAvailableIDs()
    .contains(this)

/**
 * ISO 3166-1 alpha-3 code for the country of this location.
 */
fun String.isValidCountry(): Boolean = Locale
    .getISOCountries(Locale.IsoCountryCode.PART1_ALPHA3)
    .contains(this)

/**
 * Latitude of the point in decimal degree Regex: -?[0-9]{1,2}\.[0-9]{6}
 */
fun String.isValidLatitude(): Boolean =
    matches("-?\\d{1,2}\\.\\d{6}".toRegex())

/**
 * Longitude of the point in decimal degree Regex: -?[0-9]{1,3}\.[0-9]{6}
 */
fun String.isValidLongitude(): Boolean =
    matches("-?\\d{1,3}\\.\\d{6}".toRegex())

/**
 * Text to be displayed to an end user. No markup, html etc. allowed.
 */
fun String.isRawString(): Boolean =
    !matches(".*<(\"[^\"]*\"|'[^']*'|[^'\">])*>.*".toRegex())

/**
 * Language Code ISO 639-1
 */
fun String.isValidLanguage(): Boolean =
    length <= 2

fun String.isValidTime(): Boolean =
    matches("([0-1]\\d|2[0-3]):[0-5]\\d".toRegex())

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
fun String.isValidEvseId(): Boolean = matches("(?i)[a-z]{2}\\*?[a-z\\d]{3}\\*?E[a-z\\d*]{1,31}".toRegex())