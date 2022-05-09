//  ----------
//  - WARNING -
//  ----------
//  This code is generated AND MUST NOT BE EDITED
//  ----------
package ocpi.locations.domain

/**
 * Partial representation of [ocpi.locations.domain.Image]
 */
data class ImagePartial(
  val url: String?,
  val thumbnail: String?,
  val category: ImageCategory?,
  val type: String?,
  val width: Int?,
  val height: Int?,
)

fun Image.toPartial(): ImagePartial {
   return ImagePartial(
     url = url,
    thumbnail = thumbnail,
    category = category,
    type = type,
    width = width,
    height = height
   )
}

fun List<Image>.toPartial(): List<ImagePartial> {
   return mapNotNull { it.toPartial() }
}
