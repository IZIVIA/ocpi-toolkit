package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers

import com.izivia.ocpi.toolkit.modules.locations.domain.Capability
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorType
import com.izivia.ocpi.toolkit.modules.locations.domain.Facility
import com.izivia.ocpi.toolkit.modules.locations.domain.ImageCategory
import com.izivia.ocpi.toolkit.modules.locations.domain.ParkingRestriction
import com.izivia.ocpi.toolkit.modules.locations.domain.ParkingType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

inline fun <reified T> createEnumSerializerWithOther(): KSerializer<T> where T : Enum<T> {
    val otherValue = enumValues<T>().first { it.name == "OTHER" }

    return object : KSerializer<T> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor(T::class.qualifiedName ?: "Enum", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeString(value.name)
        }

        override fun deserialize(decoder: Decoder): T {
            val value = decoder.decodeString()
            return enumValues<T>().firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: otherValue
        }
    }
}

object CapabilitySerializer : KSerializer<Capability> by createEnumSerializerWithOther()
object ConnectorTypeSerializer : KSerializer<ConnectorType> by createEnumSerializerWithOther()
object FacilitySerializer : KSerializer<Facility> by createEnumSerializerWithOther()
object ImageCategorySerializer : KSerializer<ImageCategory> by createEnumSerializerWithOther()
object ParkingRestrictionSerializer : KSerializer<ParkingRestriction> by createEnumSerializerWithOther()
object ParkingTypeSerializer : KSerializer<ParkingType> by createEnumSerializerWithOther()
