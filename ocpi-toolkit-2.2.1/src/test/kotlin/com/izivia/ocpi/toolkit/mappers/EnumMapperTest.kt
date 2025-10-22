package com.izivia.ocpi.toolkit.mappers

import com.izivia.ocpi.toolkit.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit.mappers.data.JsonMappingData
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit.serialization.deserializeObject
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.first
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class EnumMapperTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize OTHER as default enum value`(serializer: OcpiSerializer) {
        expectThat(serializer.deserializeObject<Capability>(""""WRONG""""))
            .isEqualTo(Capability.OTHER)

        expectThat(serializer.deserializeObject<ConnectorType>(""""WRONG""""))
            .isEqualTo(ConnectorType.OTHER)

        expectThat(serializer.deserializeObject<Facility>(""""WRONG""""))
            .isEqualTo(Facility.OTHER)

        expectThat(serializer.deserializeObject<ImageCategory>(""""WRONG""""))
            .isEqualTo(ImageCategory.OTHER)

        expectThat(serializer.deserializeObject<ParkingRestriction>(""""WRONG""""))
            .isEqualTo(ParkingRestriction.OTHER)

        expectThat(serializer.deserializeObject<ParkingType>(""""WRONG""""))
            .isEqualTo(ParkingType.OTHER)
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should deserialize enums when wrapper in Location`(serializer: OcpiSerializer) {
        expectThat(
            serializer.deserializeObject<Location>(
                JsonMappingData.location(
                    capabilities = "\"RESERVABLE\", \"UNKNOWN\"",
                    facilities = "\"BIKE_SHARING\", \"UNKNOWN\"",
                    parkingRestrictions = "\"CUSTOMERS\", \"UNKNOWN\"",
                    imageCategory = "\"UNKNOWN\"",
                    parkingType = "\"UNKNOWN\"",
                    connectorType = "\"UNKNOWN\"",
                ),
            ),
        )
            .and {
                get { evses }.isNotNull().first().and {
                    // capabilities
                    get { capabilities }.isNotNull()
                        .containsExactly(Capability.RESERVABLE, Capability.OTHER)

                    // parking restrictions
                    get { parkingRestrictions }.isNotNull()
                        .containsExactly(ParkingRestriction.CUSTOMERS, ParkingRestriction.OTHER)

                    // image category
                    get { images }.isNotNull().first().get { category }
                        .isEqualTo(ImageCategory.OTHER)

                    // connector type
                    get { connectors }.first().get { standard }
                        .isEqualTo(ConnectorType.OTHER)
                }

                // facilities
                get { facilities }.isNotNull().containsExactly(Facility.BIKE_SHARING, Facility.OTHER)

                // parking type
                get { parkingType }.isNotNull().isEqualTo(ParkingType.OTHER)
            }
    }
}
