package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.mappers.data.MappingData
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.isJsonEqualTo
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import com.izivia.ocpi.toolkit.serialization.serializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CommandMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize CancelReservation`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.cancelReservation, CancelReservation::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.cancelReservation)

        expectThat(serializer.serializeObject(MappingData.cancelReservation))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.cancelReservation)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize CommandResponse`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.commandResponse, CommandResponse::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.commandResponse)

        expectThat(serializer.serializeObject(MappingData.commandResponse))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.commandResponse)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize CommandResult`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.commandResult, CommandResult::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.commandResult)

        expectThat(serializer.serializeObject(MappingData.commandResult))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.commandResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize ReserveNow`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.reserveNow, ReserveNow::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.reserveNow)

        expectThat(serializer.serializeObject(MappingData.reserveNow))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.reserveNow)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize StartSession`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.startSession, StartSession::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.startSession)

        expectThat(serializer.serializeObject(MappingData.startSession))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.startSession)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize StopSession`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.stopSession, StopSession::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.stopSession)

        expectThat(serializer.serializeObject(MappingData.stopSession))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.stopSession)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize UnlockConnector`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(MappingData.unlockConnector, UnlockConnector::class.java))
            .describedAs("deserialize with explicit class")
            .isJsonEqualTo(JsonMappingData.unlockConnector)

        expectThat(serializer.serializeObject(MappingData.unlockConnector))
            .describedAs("deserialize without explicit class")
            .isJsonEqualTo(JsonMappingData.unlockConnector)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize CancelReservation`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.cancelReservation, CancelReservation::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.cancelReservation)

        expectThat(serializer.deserializeObject<CancelReservation>(JsonMappingData.cancelReservation))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.cancelReservation)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize CommandResponse`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.commandResponse, CommandResponse::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.commandResponse)

        expectThat(serializer.deserializeObject<CommandResponse>(JsonMappingData.commandResponse))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.commandResponse)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize CommandResult`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.commandResult, CommandResult::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.commandResult)

        expectThat(serializer.deserializeObject<CommandResult>(JsonMappingData.commandResult))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.commandResult)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize ReserveNow`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.reserveNow, ReserveNow::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.reserveNow)

        expectThat(serializer.deserializeObject<ReserveNow>(JsonMappingData.reserveNow))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.reserveNow)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize StartSession`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.startSession, StartSession::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.startSession)

        expectThat(serializer.deserializeObject<StartSession>(JsonMappingData.startSession))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.startSession)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize StopSession`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.stopSession, StopSession::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.stopSession)

        expectThat(serializer.deserializeObject<StopSession>(JsonMappingData.stopSession))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.stopSession)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize UnlockConnector`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject(JsonMappingData.unlockConnector, UnlockConnector::class.java))
            .describedAs("deserialize with explicit class")
            .isEqualTo(MappingData.unlockConnector)

        expectThat(serializer.deserializeObject<UnlockConnector>(JsonMappingData.unlockConnector))
            .describedAs("deserialize without explicit class")
            .isEqualTo(MappingData.unlockConnector)
    }
}
