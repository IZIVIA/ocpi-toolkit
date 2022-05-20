//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.BusinessDetails]
 */
public data class BusinessDetailsPartial(
  public val name: String?,
  public val website: String?,
  public val logo: ImagePartial?,
)

public fun BusinessDetails.toPartial(): BusinessDetailsPartial {
   return BusinessDetailsPartial(
     name = name,
    website = website,
    logo = logo?.toPartial()
   )
}

public fun List<BusinessDetails>.toPartial(): List<BusinessDetailsPartial> {
   return mapNotNull { it.toPartial() }
}
