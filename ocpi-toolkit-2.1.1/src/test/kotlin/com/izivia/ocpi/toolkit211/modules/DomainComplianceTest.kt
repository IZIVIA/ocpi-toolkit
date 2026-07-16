package com.izivia.ocpi.toolkit211.modules

import com.izivia.ocpi.toolkit211.common.TestWithSerializerProviders
import com.izivia.ocpi.toolkit211.modules.cdr.domain.CdrDimensionType
import com.izivia.ocpi.toolkit211.modules.cdr.domain.CdrPartial
import com.izivia.ocpi.toolkit211.modules.cdr.services.validate
import com.izivia.ocpi.toolkit211.modules.commands.domain.CommandResponseType
import com.izivia.ocpi.toolkit211.modules.commands.domain.ReserveNow
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.credentials.services.validate
import com.izivia.ocpi.toolkit211.modules.locations.domain.BusinessDetails
import com.izivia.ocpi.toolkit211.modules.tokens.domain.LocationReferences
import com.izivia.ocpi.toolkit211.modules.tokens.domain.Token
import com.izivia.ocpi.toolkit211.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit211.modules.tokens.domain.WhitelistType
import com.izivia.ocpi.toolkit211.modules.tokens.services.validate
import com.izivia.ocpi.toolkit211.serialization.OcpiSerializer
import com.izivia.ocpi.toolkit211.serialization.serializeObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.valiktor.ConstraintViolationException
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class DomainComplianceTest : TestWithSerializerProviders {
    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should serialize corrected 2_1_1 shapes`(serializer: OcpiSerializer) {
        val credentials = Credentials(
            token = "token",
            url = "https://example.com/versions",
            businessDetails = BusinessDetails(name = "Example Operator"),
            partyId = "EXA",
            countryCode = "NL",
        )
        expectThat(serializer.serializeObject(credentials)).isJsonEqualTo(
            """
            {
              "token": "token",
              "url": "https://example.com/versions",
              "business_details": {"name": "Example Operator"},
              "party_id": "EXA",
              "country_code": "NL"
            }
            """.trimIndent(),
        )

        val references = LocationReferences(
            locationId = "location",
            evseUids = listOf("evse"),
            connectorIds = listOf("connector"),
        )
        expectThat(serializer.serializeObject(references)).isJsonEqualTo(
            """{"location_id":"location","evse_uids":["evse"],"connector_ids":["connector"]}""",
        )

        val reserveNow = ReserveNow(
            token = Token(
                uid = "uid",
                type = TokenType.RFID,
                authId = "auth",
                issuer = "issuer",
                valid = true,
                whitelist = WhitelistType.ALLOWED,
                lastUpdated = Instant.parse("2015-06-29T22:39:09Z"),
            ),
            expiryDate = Instant.parse("2015-06-29T23:39:09Z"),
            reservationId = 42,
            locationId = "location",
            evseUid = null,
        )
        expectThat(serializer.serializeObject(reserveNow)).isJsonEqualTo(
            """
            {
              "token": {
                "uid": "uid",
                "type": "RFID",
                "auth_id": "auth",
                "issuer": "issuer",
                "valid": true,
                "whitelist": "ALLOWED",
                "last_updated": "2015-06-29T22:39:09Z"
              },
              "expiry_date": "2015-06-29T23:39:09Z",
              "reservation_id": 42,
              "location_id": "location"
            }
            """.trimIndent(),
        )
    }

    @ParameterizedTest
    @MethodSource("getAvailableOcpiSerializers")
    fun `should expose required 2_1_1 enum values`(serializer: OcpiSerializer) {
        expectThat(serializer.serializeObject(CdrDimensionType.FLAT)).isEqualTo("\"FLAT\"")
        expectThat(serializer.serializeObject(CommandResponseType.TIMEOUT)).isEqualTo("\"TIMEOUT\"")
    }

    @Test
    fun `should enforce corrected identifier limits`() {
        assertThrows<ConstraintViolationException> {
            CdrPartial(id = "x".repeat(37)).validate()
        }
        assertThrows<ConstraintViolationException> {
            LocationReferences(locationId = "x".repeat(40)).validate()
        }
        assertThrows<ConstraintViolationException> {
            LocationReferences(locationId = "location", evseUids = listOf("x".repeat(40))).validate()
        }
        assertThrows<ConstraintViolationException> {
            LocationReferences(locationId = "location", connectorIds = listOf("x".repeat(37))).validate()
        }
        assertThrows<ConstraintViolationException> {
            Credentials(
                token = "token",
                url = "https://example.com/versions",
                businessDetails = BusinessDetails(name = "Example Operator"),
                partyId = "TOOLONG",
                countryCode = "NL",
            ).validate()
        }
    }
}
