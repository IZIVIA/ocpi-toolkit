package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class VersionMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize VersionDetails`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.versionDetails, VersionDetails::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.versionDetails)

        expectThat(serializer.serializeObject(MappingData.versionDetails))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.versionDetails)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize VersionDetails`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.versionDetails, VersionDetails::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.versionDetails)

        expectThat(serializer.deserializeObject<VersionDetails>(JsonMappingData.versionDetails))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.versionDetails)
    }
}
