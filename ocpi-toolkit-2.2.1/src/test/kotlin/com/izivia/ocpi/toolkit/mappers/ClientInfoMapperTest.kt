package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.hubclientinfo.domain.ClientInfo
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ClientInfoMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ClientInfo`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.clientInfo, ClientInfo::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.clientInfo)

        expectThat(serializer.serializeObject(MappingData.clientInfo))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.clientInfo)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ClientInfo`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.clientInfo, ClientInfo::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.clientInfo)

        expectThat(serializer.deserializeObject<ClientInfo>(JsonMappingData.clientInfo))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.clientInfo)
    }
}
