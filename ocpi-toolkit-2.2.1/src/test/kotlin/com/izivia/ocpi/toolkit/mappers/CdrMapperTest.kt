package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.cdr.domain.Cdr
import com.izivia.ocpi.toolkit.modules.cdr.domain.CdrPartial
import com.izivia.ocpi.toolkit.modules.cdr.domain.toPartial
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CdrMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Cdr`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.cdr, Cdr::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.cdr)

        expectThat(serializer.serializeObject(MappingData.cdr))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.cdr)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize CdrPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.cdr.toPartial(), CdrPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.cdr)

        expectThat(serializer.serializeObject(MappingData.cdr.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.cdr)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Cdr`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.cdr, Cdr::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.cdr)

        expectThat(serializer.deserializeObject<Cdr>(JsonMappingData.cdr))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.cdr)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize CdrPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.cdr, CdrPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.cdr.toPartial())

        expectThat(serializer.deserializeObject<CdrPartial>(JsonMappingData.cdr))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.cdr.toPartial())
    }
}
