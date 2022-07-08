//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.modules.locations.domain.EnergyMixPartial
import com.izivia.ocpi.toolkit.modules.locations.domain.toPartial
import com.izivia.ocpi.toolkit.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit.modules.types.PricePartial
import com.izivia.ocpi.toolkit.modules.types.toPartial
import java.time.Instant
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff]
 */
public data class TariffPartial(
  public val id: String?,
  public val type: TariffType?,
  public val party_id: String?,
  public val country_code: String?,
  public val currency: String?,
  public val tariff_alt_text: List<DisplayTextPartial>?,
  public val tariff_alt_url: String?,
  public val min_price: PricePartial?,
  public val max_price: PricePartial?,
  public val elements: List<TariffElementPartial>?,
  public val start_date_time: Instant?,
  public val end_date_time: Instant?,
  public val energy_mix: EnergyMixPartial?,
  public val last_updated: Instant?,
)

public fun Tariff.toPartial(): TariffPartial {
   return TariffPartial(
     id = id,
    type = type,
    party_id = party_id,
    country_code = country_code,
    currency = currency,
    tariff_alt_text = tariff_alt_text?.toPartial(),
    tariff_alt_url = tariff_alt_url,
    min_price = min_price?.toPartial(),
    max_price = max_price?.toPartial(),
    elements = elements.toPartial(),
    start_date_time = start_date_time,
    end_date_time = end_date_time,
    energy_mix = energy_mix?.toPartial(),
    last_updated = last_updated
   )
}

public fun List<Tariff>.toPartial(): List<TariffPartial> {
   return mapNotNull { it.toPartial() }
}
