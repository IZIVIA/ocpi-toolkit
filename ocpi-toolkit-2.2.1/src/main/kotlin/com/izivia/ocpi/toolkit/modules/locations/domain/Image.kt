package com.izivia.ocpi.toolkit.modules.locations.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString

/**
 * This class references images related to a EVSE in terms of a file name or url. According to the roaming connection
 * between one EVSE Operator and one or more Navigation Service Providers the hosting or file exchange of image payload
 * data has to be defined. The exchange of this content data is out of scope of OCHP. However, the recommended setup is
 * a public available web server hosted and updated by the EVSE Operator. Per charge point an unlimited number of images
 * of each type is allowed. Recommended are at least two images where one is a network or provider logo and the second
 * is a station photo. If two images of the same type are defined they should be displayed additionally, not optionally.
 *
 * Photo Dimensions: The recommended dimensions for all photos is a minimum of 800 pixels wide and 600 pixels height.
 * Thumbnail representations for photos should always have the same orientation as the original with a size of 200 to
 * 200 pixels.
 *
 * Logo Dimensions: The recommended dimensions for logos are exactly 512 pixels wide and 512 pixels height. Thumbnail
 * representations for logos should be exactly 128 pixels in width and height. If not squared, thumbnails should have
 * the same orientation as the original.
 *
 * @property url URL (string(255) type following the w3.org spec. from where the image data can be fetched through a
 * web browser.
 * @property thumbnail URL (string(255) type following the w3.org spec. from where a thumbnail of the image can
 * be fetched through a web browser.
 * @property category Describes what the image is used for.
 * @property type (max-length=4) Image type like: gif, jpeg, png, svg
 * @property width (max-length=5)
 * @property height (max-length=5)
 * @constructor
 */
@Partial
data class Image(
    val url: String,
    val thumbnail: String?,
    val category: ImageCategory,
    val type: CiString,
    val width: Int?,
    val height: Int?,
)
