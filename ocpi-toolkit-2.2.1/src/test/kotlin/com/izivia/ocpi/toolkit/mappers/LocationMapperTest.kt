package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.modules.locations.domain.LocationPartial
import com.izivia.ocpi.toolkit.modules.locations.domain.toPartial
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LocationMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.location, Location::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.location())

        expectThat(serializer.serializeObject(MappingData.location))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.location())
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize LocationPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.location.toPartial(), LocationPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.location())

        expectThat(serializer.serializeObject(MappingData.location.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.location())
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.location(), Location::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.location)

        expectThat(serializer.deserializeObject<Location>(JsonMappingData.location()))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.location)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize LocationPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.location(), LocationPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.location.toPartial())

        expectThat(serializer.deserializeObject<LocationPartial>(JsonMappingData.location()))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.location.toPartial())
    }
}
