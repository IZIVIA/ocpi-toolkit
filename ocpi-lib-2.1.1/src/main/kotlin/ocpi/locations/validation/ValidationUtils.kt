package ocpi.locations.validation

/**
 * URL type following the w3.org spec. to the operator's terms and conditions
 * TODO: Improve?
 */
fun String.isValidUrl(): Boolean =
    length <= 255

/**
 * One of IANA tzdata's TZ-values representing the time zone of the location.
 * Examples: "Europe/Oslo", "Europe/Zurich". (http://www.iana.org/time-zones)
 * TODO: Improve?
 */
fun String.isValidTimeZone(): Boolean =
    length <= 255

/**
 * ISO 3166-1 alpha-3 code for the country of this location.
 * TODO: Improve?
 */
fun String.isValidCountry(): Boolean =
    length <= 3 && uppercase() == this

/**
 * Latitude of the point in decimal degree Regex: -?[0-9]{1,2}\.[0-9]{6}
 */
fun String.isValidLatitude(): Boolean =
    matches("-?[0-9]{1,2}\\.[0-9]{6}".toRegex())

/**
 * Longitude of the point in decimal degree Regex: -?[0-9]{1,3}\.[0-9]{6}
 */
fun String.isValidLongitude(): Boolean =
    matches("-?[0-9]{1,3}\\.[0-9]{6}".toRegex())

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
    matches("([0-1][0-9]|2[0-3]):[0-5][0-9]".toRegex())

/**
 * Compliant with the following specification for EVSE ID from "eMI3 standard version V1.0"
 * (http://emi3group.com/documents-links/) "Part 2: business objects." Optional because: if an EVSE ID is to be re-used
 * the EVSE ID can be removed from an EVSE that is removed (status: REMOVED)
 * TODO: Improve ?
 */
fun String.isValidEvseId(): Boolean =
    true