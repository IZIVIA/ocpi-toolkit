package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CredentialsMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Credentials`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.credentials, Credentials::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.credentials)

        expectThat(serializer.serializeObject(MappingData.credentials))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.credentials)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Credentials`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.credentials, Credentials::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.credentials)

        expectThat(serializer.deserializeObject<Credentials>(JsonMappingData.credentials))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.credentials)
    }
}
