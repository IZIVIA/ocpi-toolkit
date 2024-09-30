package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial
import com.izivia.ocpi.toolkit.common.CiString

/**
 * This class contains all the information of the signed data. Which encoding method is used, if needed, the public key and a list of
signed values.
 * @property encodingMethod The name of the encoding used in the SignedData field. This is the
name given to the encoding by a company or group of companies. See
note below.
 * @property encodingMethodVersion Version of the EncodingMethod (when applicable)
 * @property publicKey Public key used to sign the data, base64 encoded.
 * @property signedValues  One or more signed values.
 * @property url URL that can be shown to an EV driver. This URL gives the EV driver
the possibility to check the signed data from a charging session.
 */
@Partial
data class SignedData(
    val encodingMethod: CiString,
    val encodingMethodVersion: Int? = null,
    val publicKey: CiString? = null,
    val signedValues: List<SignedValue>,
    val url: CiString? = null,
)
