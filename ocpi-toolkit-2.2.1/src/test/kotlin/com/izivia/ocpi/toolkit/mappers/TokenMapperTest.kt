package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TokenMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize Token`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.token, Token::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.token)

        expectThat(serializer.serializeObject(MappingData.token))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.token)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize TokenPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.token.toPartial(), TokenPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.token)

        expectThat(serializer.serializeObject(MappingData.token.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.token)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize AuthorizationInfo`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.authorizationInfo, AuthorizationInfo::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.authorizationInfo)

        expectThat(serializer.serializeObject(MappingData.authorizationInfo))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.authorizationInfo)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize AuthorizationInfoPartial`(serializer: OcpiSerializer) {
        expectThat(
            serializer.serializeObject(
                MappingData.authorizationInfo.toPartial(),
                AuthorizationInfoPartial::class.java,
            ),
        )
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.authorizationInfo)

        expectThat(serializer.serializeObject(MappingData.authorizationInfo.toPartial()))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.authorizationInfo)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize Token`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.token, Token::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.token)

        expectThat(serializer.deserializeObject<Token>(JsonMappingData.token))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.token)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize TokenPartial`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.token, TokenPartial::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.token.toPartial())

        expectThat(serializer.deserializeObject<TokenPartial>(JsonMappingData.token))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.token.toPartial())
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize AuthorizationInfo`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.authorizationInfo, AuthorizationInfo::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.authorizationInfo)

        expectThat(serializer.deserializeObject<AuthorizationInfo>(JsonMappingData.authorizationInfo))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.authorizationInfo)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize AuthorizationInfoPartial`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject(JsonMappingData.authorizationInfo, AuthorizationInfoPartial::class.java),
        )
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.authorizationInfo.toPartial())

        expectThat(serializer.deserializeObject<AuthorizationInfoPartial>(JsonMappingData.authorizationInfo))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.authorizationInfo.toPartial())
    }
}
