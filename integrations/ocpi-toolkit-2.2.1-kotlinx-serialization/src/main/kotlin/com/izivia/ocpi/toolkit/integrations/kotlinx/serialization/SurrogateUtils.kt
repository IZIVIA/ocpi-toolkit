package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

fun <I, T> KSerializer<I>.mapped(
    convertForEncoding: (T) -> I,
    convertForDecoding: (I) -> T,
): KSerializer<T> = object : KSerializer<T> {
    override val descriptor: SerialDescriptor = this@mapped.descriptor

    override fun deserialize(decoder: Decoder): T = convertForDecoding(this@mapped.deserialize(decoder))

    override fun serialize(encoder: Encoder, value: T) = this@mapped.serialize(encoder, convertForEncoding(value))
}
