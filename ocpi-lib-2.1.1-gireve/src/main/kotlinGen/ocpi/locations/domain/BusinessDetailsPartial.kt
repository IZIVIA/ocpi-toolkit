//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.BusinessDetails]
 */
data class BusinessDetailsPartial(
  val name: String?,
  val website: String?,
  val logo: ImagePartial?,
)

fun BusinessDetails.toPartial(): BusinessDetailsPartial {
   return BusinessDetailsPartial(
     name = name,
    website = website,
    logo = logo?.toPartial()
   )
}

fun List<BusinessDetails>.toPartial(): List<BusinessDetailsPartial> {
   return mapNotNull { it.toPartial() }
}
