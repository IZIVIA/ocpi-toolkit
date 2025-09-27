package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.tariff.domain.Tariff
import com.izivia.ocpi.toolkit.modules.tariff.domain.TariffPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.toPartial
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TariffMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.tariff, Tariff::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.tariff)

        expectThat(serializer.serializeObject(MappingData.tariff))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.tariff)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize LocationPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.tariff.toPartial(), TariffPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.tariff)

        expectThat(serializer.serializeObject(MappingData.tariff.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.tariff)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.tariff, Tariff::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.tariff)

        expectThat(serializer.deserializeObject<Tariff>(JsonMappingData.tariff))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.tariff)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Tariff`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.tariff, TariffPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.tariff.toPartial())

        expectThat(serializer.deserializeObject<TariffPartial>(JsonMappingData.tariff))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.tariff.toPartial())
    }
}
