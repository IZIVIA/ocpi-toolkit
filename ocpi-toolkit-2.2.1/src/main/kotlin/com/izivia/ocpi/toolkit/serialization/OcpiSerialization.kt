package com.izivia.ocpi.toolkit.serialization

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import java.util.ServiceLoader

/**
 * Abstract class representing an OCPI serializer responsible for serializing and deserializing objects.
 *
 */
abstract class OcpiSerializer {
    abstract val name: String

    abstract fun <T> serializeObject(obj: T, clazz: Class<T>): String
    abstract fun <T> serializeList(obj: List<T>, clazz: Class<T>): String
    abstract fun <T> serializeOcpiResponse(obj: OcpiResponseBody<T>, clazz: Class<T>): String
    abstract fun <T> serializeOcpiResponseList(obj: OcpiResponseBody<List<T>>, clazz: Class<T>): String
    abstract fun <T> deserializeObject(data: String?, clazz: Class<T>): T
    abstract fun <T> deserializeList(data: String?, clazz: Class<T>): List<T>
    abstract fun <T> deserializeOcpiResponse(data: String?, clazz: Class<T>): OcpiResponseBody<T>
    abstract fun <T> deserializeOcpiResponseList(data: String?, clazz: Class<T>): OcpiResponseBody<List<T>>

    override fun toString(): String = name
}

inline fun <reified T> OcpiSerializer.serializeObject(obj: T): String =
    serializeObject(obj, T::class.java)

inline fun <reified T> OcpiSerializer.serializeList(obj: List<T>): String =
    serializeList(obj, T::class.java)

inline fun <reified T> OcpiSerializer.serializeOcpiResponse(obj: OcpiResponseBody<T>): String =
    serializeOcpiResponse(obj, T::class.java)

inline fun <reified T> OcpiSerializer.serializeOcpiResponseList(obj: OcpiResponseBody<List<T>>): String =
    serializeOcpiResponseList(obj, T::class.java)

inline fun <reified T> OcpiSerializer.deserializeObject(data: String?): T =
    deserializeObject(data, T::class.java)

inline fun <reified T> OcpiSerializer.deserializeList(data: String?): List<T> =
    deserializeList(data, T::class.java)

inline fun <reified T> OcpiSerializer.deserializeOcpiResponse(data: String?): OcpiResponseBody<T> =
    deserializeOcpiResponse(data, T::class.java)

inline fun <reified T> OcpiSerializer.deserializeOcpiResponseList(data: String?): OcpiResponseBody<List<T>> =
    deserializeOcpiResponseList(data, T::class.java)

/**
 * Registry for OCPI serializers that loads available implementations using ServiceLoader. If you want to add your
 * own implementation and a file in: META-INF/services/com.izivia.ocpi.toolkit.serialization.OcpiSerializer specifying
 * where your implementation is located.
 */
object OcpiSerializationRegistry {
    val serializers: List<OcpiSerializer> by lazy {
        ServiceLoader.load(OcpiSerializer::class.java).toList()
    }

    /**
     * Gets the first available serializer or throws if none found
     * @throws IllegalStateException if no serializer implementation is found in classpath
     */
    fun getSerializer(): OcpiSerializer {
        return serializers
            .firstOrNull()
            ?: throw IllegalStateException("No serializer provider found. Please add one to classpath")
    }
}

/**
 * Serializer used by the lib
 */
var mapper = OcpiSerializationRegistry.getSerializer()
