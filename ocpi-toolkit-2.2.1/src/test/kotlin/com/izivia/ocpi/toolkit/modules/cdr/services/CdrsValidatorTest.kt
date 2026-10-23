package com.izivia.ocpi.toolkit.modules.cdr.services

import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorFormat
import com.izivia.ocpi.toolkit.modules.locations.domain.ConnectorType
import com.izivia.ocpi.toolkit.modules.locations.domain.GeoLocation
import com.izivia.ocpi.toolkit.modules.locations.domain.PowerType
import com.izivia.ocpi.toolkit.modules.tokens.domain.TokenType
import com.izivia.ocpi.toolkit.modules.types.Price
import org.junit.jupiter.api.Test
import strikt.api.expectCatching
import strikt.assertions.isFailure
import strikt.assertions.isSuccess
import java.math.BigDecimal
import java.time.Instant

class CdrsValidatorTest {
    private val timestamp = Instant.parse("2024-01-01T10:00:00Z")

    private val nonCreditCdr = Cdr(
        countryCode = "FR",
        partyId = "ABC",
        id = "cdr-1",
        startDateTime = timestamp,
        endDateTime = timestamp,
        cdrToken = CdrToken(
            countryCode = "FR",
            partyId = "ABC",
            uid = "uid",
            type = TokenType.RFID,
            contractId = "contractId",
        ),
        authMethod = AuthMethod.WHITELIST,
        cdrLocation = CdrLocation(
            id = "location-1",
            address = "address",
            city = "city",
            country = "FRA",
            coordinates = GeoLocation(latitude = "48.856614", longitude = "2.352222"),
            evseUid = "evse-1",
            evseId = "FR*ABC*E1",
            connectorId = "1",
            connectorStandard = ConnectorType.IEC_62196_T2,
            connectorFormat = ConnectorFormat.SOCKET,
            connectorPowerType = PowerType.AC_3_PHASE,
        ),
        currency = "EUR",
        tariffs = null,
        chargingPeriods = listOf(
            ChargingPeriod(
                startDateTime = timestamp,
                dimensions = listOf(CdrDimension(type = CdrDimensionType.ENERGY, volume = BigDecimal("10"))),
            ),
        ),
        totalCost = Price(exclVat = BigDecimal("10.00"), inclVat = BigDecimal("12.00")),
        totalEnergy = BigDecimal("10"),
        totalTime = BigDecimal("1"),
        lastUpdated = timestamp,
    )

    private val creditCdr = nonCreditCdr.copy(
        credit = true,
        creditReferenceId = "cdr-1",
        totalCost = Price(exclVat = BigDecimal("-10.00"), inclVat = BigDecimal("-12.00")),
    )

    @Test
    fun `non-credit cdr id is limited to 36 characters`() {
        expectCatching {
            nonCreditCdr.copy(id = "a".repeat(36)).validate()
        }.isSuccess()

        expectCatching {
            nonCreditCdr.copy(id = "a".repeat(37)).validate()
        }.isFailure()

        expectCatching {
            nonCreditCdr.copy(id = "a".repeat(37), credit = false).validate()
        }.isFailure()
    }

    @Test
    fun `credit cdr id is limited to 39 characters`() {
        expectCatching {
            creditCdr.copy(id = "a".repeat(39)).validate()
        }.isSuccess()

        expectCatching {
            creditCdr.copy(id = "a".repeat(40)).validate()
        }.isFailure()
    }

    @Test
    fun `credit cdr requires credit_reference_id`() {
        expectCatching {
            creditCdr.copy(creditReferenceId = null).validate()
        }.isFailure()
    }

    @Test
    fun `partial cdr rules only apply when their fields are present`() {
        expectCatching {
            CdrPartial(
                countryCode = null, partyId = null, id = null, startDateTime = null, endDateTime = null,
                sessionId = null, cdrToken = null, authMethod = null, authorizationReference = null,
                cdrLocation = null, meterId = null, currency = null, tariffs = null, chargingPeriods = null,
                signedData = null, totalCost = null, totalFixedCost = null, totalEnergy = null,
                totalEnergyCost = null, totalTime = null, totalTimeCost = null, totalParkingTime = null,
                totalParkingCost = null, totalReservationCost = null, remark = null, invoiceReferenceId = null,
                credit = null, creditReferenceId = null, homeChargingCompensation = null, lastUpdated = null,
            ).validate()
        }.isSuccess()
    }
}
