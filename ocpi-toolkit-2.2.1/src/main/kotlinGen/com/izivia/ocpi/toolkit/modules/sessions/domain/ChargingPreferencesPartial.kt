//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.sessions.domain

import java.time.Instant

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences]
 */
public data class ChargingPreferencesPartial(
    public val profile_type: ProfileType?,
    public val departure_time: Instant?,
    public val energy_need: Int?,
    public val discharge_allowed: Boolean?,
)

public fun ChargingPreferences.toPartial(): ChargingPreferencesPartial {
    return ChargingPreferencesPartial(
        profile_type = profile_type,
        departure_time = departure_time,
        energy_need = energy_need,
        discharge_allowed = discharge_allowed
    )
}

public fun List<ChargingPreferences>.toPartial(): List<ChargingPreferencesPartial> {
    return mapNotNull { it.toPartial() }
}
