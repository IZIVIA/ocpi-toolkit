//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.DisplayText]
 */
data class DisplayTextPartial(
  val language: String?,
  val text: String?,
)

fun DisplayText.toPartial(): DisplayTextPartial {
   return DisplayTextPartial(
     language = language,
    text = text
   )
}

fun List<DisplayText>.toPartial(): List<DisplayTextPartial> {
   return mapNotNull { it.toPartial() }
}
