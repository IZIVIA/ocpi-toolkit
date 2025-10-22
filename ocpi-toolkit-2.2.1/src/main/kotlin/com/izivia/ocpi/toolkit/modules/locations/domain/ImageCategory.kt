package com.izivia.ocpi.toolkit.modules.locations.domain

/**
 * The category of an image to obtain the correct usage in a user presentation. The category has to be set accordingly
 * to the image content in order to guarantee the right usage.
 */
enum class ImageCategory {
    /**
     * Photo of the physical device that contains one or more EVSEs.
     */
    CHARGER,

    /**
     * Location entrance photo. Should show the car entrance to the location from street side.
     */
    ENTRANCE,

    /**
     * Location overview photo.
     */
    LOCATION,

    /**
     * Logo of an associated roaming network to be displayed with the EVSE for example in lists, maps and detailed
     * information view
     */
    NETWORK,

    /**
     * Logo of the charge points operator, for example a municipality, to be displayed with the EVSEs detailed
     * information view or in lists and maps, if no networkLogo is present
     */
    OPERATOR,

    /**
     * Other
     */
    OTHER,

    /**
     * Logo of the charge points owner, for example a local store, to be displayed with the EVSEs detailed information
     * view
     */
    OWNER,
}
