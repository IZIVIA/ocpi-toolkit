//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

import kotlin.Int
import kotlin.String
import kotlin.collections.List

/**
 * Partial representation of [ocpi.locations.domain.Image]
 */
public data class ImagePartial(
  public val url: String?,
  public val thumbnail: String?,
  public val category: ImageCategory?,
  public val type: String?,
  public val width: Int?,
  public val height: Int?,
)

public fun Image.toPartial(): ImagePartial {
   return ImagePartial(
     url = url,
    thumbnail = thumbnail,
    category = category,
    type = type,
    width = width,
    height = height
   )
}

public fun List<Image>.toPartial(): List<ImagePartial> {
   return mapNotNull { it.toPartial() }
}
