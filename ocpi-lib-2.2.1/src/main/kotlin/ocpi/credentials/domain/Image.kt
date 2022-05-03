package ocpi.credentials.domain

/**
 * This class references an image related to an EVSE in terms of a file name or url. According to the roaming connection
 * between one EVSE Operator and one or more Navigation Service Providers, the hosting or file exchange of image payload
 * data has to be defined. The exchange of this content data is out of scope of OCPI. However, the recommended setup is
 * a public available web server hosted and updated by the EVSE Operator. Per charge point an unlimited number of images
 * of each type is allowed.
 * Recommended are at least two images where one is a network or provider logo and the second is a station photo. If two
 * images of the same type are defined, not only one should be selected but both should be displayed together.
 *
 * Photo Dimensions: The recommended dimensions for all photos is a minimum width of 800 pixels and a minimum height of
 * 600 pixels. Thumbnail should always have the same orientation as the original photo with a size of 200 by 200 pixels.
 *
 * Logo Dimensions: The recommended dimensions for logos are exactly 512 pixels in width height. Thumbnail
 * representations of logos should be exactly 128 pixels in width and height. If not squared, thumbnails should have the
 * same orientation as the original.
 *
 * @property url URL from where the image data can be fetched through a web browser.
 * @property thumbnail URL from where a thumbnail of the image can be fetched through a web browser.
 * @property category Describes what the image is used for.
 * @property type (max-length=4) Image type like: gif, jpeg, png, svg
 * @property width (max-length=5)
 * @property height (max-length=5)
 */
data class Image(
    val url: String,
    val thumbnail: String?,
    val category: ImageCategory,
    val type: CiString,
    val width: Int?,
    val height: Int?
)