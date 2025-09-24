package com.izivia.ocpi.toolkit.integrations.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.integrations.jackson.mixins.*
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import java.time.Instant

class JacksonOcpiSerializer : OcpiSerializer() {
    private val mapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build(),
        )
        .registerModule(JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
        .registerModule(
            SimpleModule()
                .addSerializer(Instant::class.java, OcpiInstantSerializer())
                .addDeserializer(Instant::class.java, OcpiInstantDeserializer()),
        )
        .addMixIn(Capability::class.java, CapacityMixin::class.java)
        .addMixIn(ConnectorType::class.java, ConnectorTypeMixin::class.java)
        .addMixIn(Facility::class.java, FacilityMixin::class.java)
        .addMixIn(ImageCategory::class.java, ImageCategoryMixin::class.java)
        .addMixIn(ParkingRestriction::class.java, ParkingRestrictionMixin::class.java)
        .addMixIn(ParkingType::class.java, ParkingTypeMixin::class.java)
        .addMixIn(Connector::class.java, ConnectorMixin::class.java)

    override val name: String = "jackson"

    override fun <T> serializeObject(obj: T, clazz: Class<T>): String =
        mapper.writeValueAsString(obj)

    override fun <T> serializeList(obj: List<T>, clazz: Class<T>): String =
        mapper.writeValueAsString(obj)

    override fun <T> serializeOcpiResponse(obj: OcpiResponseBody<T>, clazz: Class<T>): String =
        mapper.writeValueAsString(obj)

    override fun <T> serializeOcpiResponseList(obj: OcpiResponseBody<List<T>>, clazz: Class<T>): String =
        mapper.writeValueAsString(obj)

    override fun <T> deserializeObject(data: String?, clazz: Class<T>): T =
        mapper.readValue(data, clazz)

    override fun <T> deserializeList(data: String?, clazz: Class<T>): List<T> {
        val listType = mapper.typeFactory.constructCollectionType(List::class.java, clazz)
        return mapper.readValue(data, listType)
    }

    override fun <T> deserializeOcpiResponse(data: String?, clazz: Class<T>): OcpiResponseBody<T> {
        val responseType = mapper.typeFactory.constructParametricType(OcpiResponseBody::class.java, clazz)
        return mapper.readValue(data, responseType)
    }

    override fun <T> deserializeOcpiResponseList(data: String?, clazz: Class<T>): OcpiResponseBody<List<T>> {
        val listType = mapper.typeFactory.constructCollectionType(List::class.java, clazz)
        val responseType = mapper.typeFactory.constructParametricType(OcpiResponseBody::class.java, listType)
        return mapper.readValue(data, responseType)
    }
}
