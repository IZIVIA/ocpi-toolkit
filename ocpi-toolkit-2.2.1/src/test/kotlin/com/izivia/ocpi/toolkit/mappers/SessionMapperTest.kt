package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.sessions.domain.toPartial
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SessionMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.session, Session::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.session)

        expectThat(serializer.serializeObject(MappingData.session))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.session)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize LocationPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.session.toPartial(), SessionPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.session)

        expectThat(serializer.serializeObject(MappingData.session.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.session)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Location`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.session, Session::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.session)

        expectThat(serializer.deserializeObject<Session>(JsonMappingData.session))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.session)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Session`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.session, SessionPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.session.toPartial())

        expectThat(serializer.deserializeObject<SessionPartial>(JsonMappingData.session))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.session.toPartial())
    }
}
