package com.izivia.ocpi.toolkit.modules.cdr.domain

import com.izivia.ocpi.toolkit.annotations.Partial


/**
 * This class contains all the information of the signed data. Which encoding method is used, if needed, the public key and a list of
signed values.
 * @property encoding_method The name of the encoding used in the SignedData field. This is the
name given to the encoding by a company or group of companies. See
note below.
 * @property encoding_method_version Version of the EncodingMethod (when applicable)
 * @property public_key Public key used to sign the data, base64 encoded.
 * @property signed_values  One or more signed values.
 * @property url URL that can be shown to an EV driver. This URL gives the EV driver
the possibility to check the signed data from a charging session.
 */
@Partial
data class SignedData(
    val encoding_method: String,
    val encoding_method_version: Int,
    val public_key: String? = null,
    val signed_values: List<SignedValue>,
    val url: String? = null,
)
