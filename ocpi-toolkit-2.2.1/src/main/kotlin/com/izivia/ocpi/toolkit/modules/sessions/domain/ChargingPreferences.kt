package com.izivia.ocpi.toolkit.modules.sessions.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import java.time.Instant

/**
 * Contains the charging preferences of an EV driver.
 *
 * @property profile_type Type of Smart Charging Profile selected by the driver. The ProfileType has to be supported at
 * the Connector and for every supported ProfileType, a Tariff MUST be provided. This gives the EV driver the option
 * between different pricing options.
 * @property departure_time Expected departure. The driver has given this Date/Time as expected departure moment.
 * It is only an estimation and not necessarily the Date/Time of the actual departure.
 * @property energy_need Requested amount of energy in kWh. The EV driver wants to have this amount of energy charged.
 * @property discharge_allowed The driver allows their EV to be discharged when needed, as long as the other preferences
 * are met: EV is charged with the preferred energy (energy_need) until the preferred departure moment (departure_time).
 * Default if omitted: false
 */
@Partial
data class ChargingPreferences(
    val profile_type: ProfileType,
    val departure_time: Instant? = null,
    val energy_need: Int? = null,
    val discharge_allowed: Boolean? = false
)
