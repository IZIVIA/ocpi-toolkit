package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.locations.domain.Location
import com.izivia.ocpi.toolkit.serialization.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class OcpiResponseMapperTest : TestWithSerializerProviders {

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize OcpiResponseBody`(serializer: OcpiSerializer) {
        expectThat(
            serializer.serializeOcpiResponse(
                MappingData.ocpiResponseBody(MappingData.location),
                Location::class.java,
            ),
        )
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.ocpiResponseBody(JsonMappingData.location()))

        expectThat(serializer.serializeOcpiResponse(MappingData.ocpiResponseBody(MappingData.location)))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.ocpiResponseBody(JsonMappingData.location()))
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize OcpiResponseBody list`(serializer: OcpiSerializer) {
        expectThat(
            serializer.serializeOcpiResponseList(
                MappingData.ocpiResponseBody(listOf(MappingData.location)),
                Location::class.java,
            ),
        )
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.ocpiResponseBody("""[${JsonMappingData.location()}]"""))

        expectThat(
            serializer.serializeOcpiResponseList(
                MappingData.ocpiResponseBody(listOf(MappingData.location)),
            ),
        )
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.ocpiResponseBody("""[${JsonMappingData.location()}]"""))
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize OcpiResponseBody`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeOcpiResponse(
                JsonMappingData.ocpiResponseBody(JsonMappingData.location()),
                Location::class.java,
            ),
        )
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.ocpiResponseBody(MappingData.location))

        expectThat(
            serializer.deserializeOcpiResponse<Location>(
                JsonMappingData.ocpiResponseBody(JsonMappingData.location()),
            ),
        )
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.ocpiResponseBody(MappingData.location))
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize OcpiResponseBody list`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeOcpiResponseList(
                JsonMappingData.ocpiResponseBody("""[${JsonMappingData.location()}]"""),
                Location::class.java,
            ),
        )
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.ocpiResponseBody(listOf(MappingData.location)))

        expectThat(
            serializer.deserializeOcpiResponseList<Location>(
                JsonMappingData.ocpiResponseBody("""[${JsonMappingData.location()}]"""),
            ),
        )
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.ocpiResponseBody(listOf(MappingData.location)))
    }
}
