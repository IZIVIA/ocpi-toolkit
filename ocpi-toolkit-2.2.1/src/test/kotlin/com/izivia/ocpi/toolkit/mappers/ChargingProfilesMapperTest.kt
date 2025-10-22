package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ActiveChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResponse
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ChargingProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.ClearProfileResult
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.SetChargingProfile
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ChargingProfilesMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ActiveChargingProfileResult`(serializer: OcpiSerializer) {
        expectThat(
            serializer.serializeObject(
                MappingData.activeChargingProfileResult,
                ActiveChargingProfileResult::class.java,
            ),
        )
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.activeChargingProfileResult)

        expectThat(serializer.serializeObject(MappingData.activeChargingProfileResult))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.activeChargingProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ChargingProfileResponse`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.chargingProfileResponse, ChargingProfileResponse::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.chargingProfileResponse)

        expectThat(serializer.serializeObject(MappingData.chargingProfileResponse))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.chargingProfileResponse)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ChargingProfileResult`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.chargingProfileResult, ChargingProfileResult::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.chargingProfileResult)

        expectThat(serializer.serializeObject(MappingData.chargingProfileResult))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.chargingProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ClearProfileResult`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.clearProfileResult, ClearProfileResult::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.clearProfileResult)

        expectThat(serializer.serializeObject(MappingData.clearProfileResult))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.clearProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize SetChargingProfile`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.setChargingProfile, SetChargingProfile::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.setChargingProfile)

        expectThat(serializer.serializeObject(MappingData.setChargingProfile))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.setChargingProfile)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ActiveChargingProfileResult`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject(
                JsonMappingData.activeChargingProfileResult,
                ActiveChargingProfileResult::class.java,
            ),
        )
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.activeChargingProfileResult)

        expectThat(
            serializer.deserializeObject<ActiveChargingProfileResult>(JsonMappingData.activeChargingProfileResult),
        )
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.activeChargingProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ChargingProfileResponse`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject(JsonMappingData.chargingProfileResponse, ChargingProfileResponse::class.java),
        )
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.chargingProfileResponse)

        expectThat(serializer.deserializeObject<ChargingProfileResponse>(JsonMappingData.chargingProfileResponse))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.chargingProfileResponse)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ChargingProfileResult`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject(JsonMappingData.chargingProfileResult, ChargingProfileResult::class.java),
        )
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.chargingProfileResult)

        expectThat(serializer.deserializeObject<ChargingProfileResult>(JsonMappingData.chargingProfileResult))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.chargingProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ClearProfileResult`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.clearProfileResult, ClearProfileResult::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.clearProfileResult)

        expectThat(serializer.deserializeObject<ClearProfileResult>(JsonMappingData.clearProfileResult))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.clearProfileResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize SetChargingProfile`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.setChargingProfile, SetChargingProfile::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.setChargingProfile)

        expectThat(serializer.deserializeObject<SetChargingProfile>(JsonMappingData.setChargingProfile))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.setChargingProfile)
    }
}
