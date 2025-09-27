package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.locations.domain.Connector
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ConnectorMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Connector and ignore null values inside of tariffId`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject<Connector>(
                JsonMappingData.connector("""["tariffId", null]"""),
            ),
        ).isEqualTo(MappingData.connector)
    }
}
