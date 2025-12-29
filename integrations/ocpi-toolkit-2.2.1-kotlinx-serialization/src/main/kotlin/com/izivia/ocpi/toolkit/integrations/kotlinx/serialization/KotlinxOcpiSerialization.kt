package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers.*
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.math.BigDecimal
import java.time.Instant
import dev.gallon.konstruct.generated.ocpiSerializersModule as generatedOcpiSerializersModule

// By doing this, the generated serializers module is exposed by the OCPI lib and not konstruct lib (which would be
// weird for any user of the ocpi lib)
val ocpiSerializersModule = generatedOcpiSerializersModule

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSerializationApi::class)
class KotlinxOcpiSerialization : OcpiSerializer() {
    override val name: String = "kotlinx.serialization"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
        isLenient = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        explicitNulls = false
        useArrayPolymorphism = false
        namingStrategy = JsonNamingStrategy.SnakeCase

        serializersModule = SerializersModule {
            // Ocpi domain (generated)
            include(ocpiSerializersModule)

            // Primitives
            contextual(BigDecimal::class, BigDecimalSerializer)
            contextual(Instant::class, InstantSerializer)

            // Enums
            contextual(Capability::class, CapabilitySerializer)
            contextual(ConnectorType::class, ConnectorTypeSerializer)
            contextual(Facility::class, FacilitySerializer)
            contextual(ImageCategory::class, ImageCategorySerializer)
            contextual(ParkingRestriction::class, ParkingRestrictionSerializer)
            contextual(ParkingType::class, ParkingTypeSerializer)
        }
    }

    override fun <T> serializeObject(obj: T, clazz: Class<T>): String {
        return json.encodeToString(clazz.getSerializer(), obj)
    }

    override fun <T> serializeList(obj: List<T>, clazz: Class<T>): String {
        val innerSerializer = clazz.getSerializer()
        val serializer = ListSerializer(innerSerializer)
        return json.encodeToString(serializer, obj)
    }

    override fun <T> serializeOcpiResponse(obj: OcpiResponseBody<T>, clazz: Class<T>): String {
        val innerSerializer = clazz.getSerializer()
        val responseSerializer = OcpiResponseBodySerializer(innerSerializer)
        return json.encodeToString(responseSerializer, obj)
    }

    override fun <T> serializeOcpiResponseList(obj: OcpiResponseBody<List<T>>, clazz: Class<T>): String {
        val innerSerializer = clazz.getSerializer()
        val listSerializer = ListSerializer(innerSerializer)
        val responseSerializer = OcpiResponseBodySerializer(listSerializer)
        return json.encodeToString(responseSerializer, obj)
    }

    override fun <T> deserializeObject(data: String?, clazz: Class<T>): T {
        requireNotNull(data) { "Data cannot be null" }
        return json.decodeFromString(clazz.getSerializer(), data)
    }

    override fun <T> deserializeList(data: String?, clazz: Class<T>): List<T> {
        requireNotNull(data) { "Data cannot be null" }
        val innerSerializer = clazz.getSerializer()
        val serializer = ListSerializer(innerSerializer)
        return json.decodeFromString(serializer, data)
    }

    override fun <T> deserializeOcpiResponse(data: String?, clazz: Class<T>): OcpiResponseBody<T> {
        requireNotNull(data) { "Data cannot be null" }
        val innerSerializer = clazz.getSerializer()
        val responseSerializer = OcpiResponseBodySerializer(innerSerializer)
        return json.decodeFromString(responseSerializer, data)
    }

    override fun <T> deserializeOcpiResponseList(data: String?, clazz: Class<T>): OcpiResponseBody<List<T>> {
        requireNotNull(data) { "Data cannot be null" }
        val innerSerializer = clazz.getSerializer()
        val listSerializer = ListSerializer(innerSerializer)
        val responseSerializer = OcpiResponseBodySerializer(listSerializer)
        return json.decodeFromString(responseSerializer, data)
    }

    private fun <T> Class<T>.getSerializer(): KSerializer<T> = when (this) {
        // When directly deserializing an Enum, the serializer used is the built-in one:
        // kotlinx.serialization.internal.EnumSerializer<T>. We instead want our custom
        // serializer if we have one
        Capability::class.java -> CapabilitySerializer
        ConnectorType::class.java -> ConnectorTypeSerializer
        Facility::class.java -> FacilitySerializer
        ImageCategory::class.java -> ImageCategorySerializer
        ParkingRestriction::class.java -> ParkingRestrictionSerializer
        ParkingType::class.java -> ParkingTypeSerializer
        else -> json.serializersModule.serializer(this)
    } as KSerializer<T>
}
