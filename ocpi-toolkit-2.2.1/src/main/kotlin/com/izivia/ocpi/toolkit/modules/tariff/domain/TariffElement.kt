package com.izivia.ocpi.toolkit.modules.tariff.domain

import com.izivia.ocpi.toolkit.annotations.Partial

/**
 * @property price_components List of price components that describe the pricing of a tariff.
 * @property restrictions Restrictions that describe the applicability of a tariff.
 */

@Partial
data class TariffElement(
    val price_components: List<PriceComponent>,
    val restrictions: TariffRestrictions? = null,
)
