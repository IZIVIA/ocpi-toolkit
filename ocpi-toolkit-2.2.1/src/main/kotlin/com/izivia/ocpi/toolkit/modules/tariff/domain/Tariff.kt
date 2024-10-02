package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString
import com.izivia.ocpi.toolkit.common.URL
import com.izivia.ocpi.toolkit.modules.locations.domain.EnergyMix
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.Price
import java.time.Instant

/**
 * A Tariff object consists of a list of one or more Tariff Elements, which can be used to create complex Tariff
 * structures.
 *
 * @property id (max-length=36) Uniquely identifies the tariff within the CPO’s platform (and suboperator platforms).
 * @property type Defines the type of the tar iff. This allows for distinction in case of given Charging Preferences.
 * When omitted, this tariff is valid for all sessions
 * @property partyId (max-length=3) CPO ID of the CPO that owns this Tariff (following the ISO-15118 standard).
 * @property countryCode (max-length=2) ISO-3166 alpha-2 country code of the CPO that owns this Tariff.
 * @property currency (max-length=3) ISO-4217 code of the currency of this tariff.
 * @property tariffAltText List of multi-language alternative tariff info texts.
 * @property tariffAltUrl URL to a web page that contains an explanation of the tariff information in human readable
 * form.
 * @property minPrice When this field is set, a Charging Session with this tariff will at least cost this
 * amount. This is different from a FLAT fee (Start Tariff, Transaction Fee), as a
 * FLAT fee is a fixed amount that has to be paid for any Charging Session. A
 * minimum price indicates that when the cost of a Charging Session is lower than
 * this amount, the cost of the Session will be equal to this amount. (Also see note
 * below)
 * @property maxPrice When this field is set, a Charging Session with this tariff will NOT cost more than
 * this amount. (See note below)
 * @property elements List of Tariff Elements.
 * @property startDateTime The time when this tariff becomes active, in UTC, time_zone field of the Location can be
 * used to convert to local time. Typically used for a new tariff that is already given with the location, before it
 * becomes active. (See note below)
 * @property endDateTime The time after which this tariff is no longer valid, in UTC, time_zone field if the
 * Location can be used to convert to local time. Typically used when this tariff is
 * going to be replaced with a different tariff in the near future. (See note below)
 * @property energyMix Details on the energy supplied with this tariff
 * @property lastUpdated Timestamp when this Tariff was last updated (or created).
 */

@Partial
data class Tariff(
    val id: CiString,
    val type: TariffType? = null,
    val partyId: CiString,
    val countryCode: CiString,
    val currency: String,
    val tariffAltText: List<DisplayText>? = null,
    val tariffAltUrl: URL? = null,
    val minPrice: Price? = null,
    val maxPrice: Price? = null,
    val elements: List<TariffElement>,
    val startDateTime: Instant? = null,
    val endDateTime: Instant? = null,
    val energyMix: EnergyMix? = null,
    val lastUpdated: Instant,
)
