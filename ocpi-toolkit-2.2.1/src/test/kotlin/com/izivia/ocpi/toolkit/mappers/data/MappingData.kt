package com.izivia.ocpi.toolkit.mappers.data

import com.izivia.ocpi.toolkit.common.OcpiResponseBody
import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.*
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.credentials.domain.Role
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.ProfileType
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionStatusType
import com.izivia.ocpi.toolkit.modules.tariff.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.Price
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.InterfaceRole
import com.izivia.ocpi.toolkit.modules.versions.domain.ModuleID
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.Instant

object MappingData {
    val timestamp: Instant = Instant.parse("2025-01-02T13:45:59.708Z")

    val displayText = DisplayText(
        language = "language",
        text = "text",
    )
    val image = Image(
        url = "url",
        thumbnail = "thumbnail",
        category = ImageCategory.LOCATION,
        type = "image/png",
        width = 123,
        height = 456,
    )
    val businessDetails = BusinessDetails(
        name = "name",
        website = "https://www.example.com",
        logo = image,
    )

    val exceptionalPeriod = ExceptionalPeriod(
        periodBegin = timestamp,
        periodEnd = Instant.parse("2025-02-01T11:00:00.000Z"),
    )

    val connector = Connector(
        id = "id",
        standard = ConnectorType.IEC_62196_T2,
        format = ConnectorFormat.SOCKET,
        powerType = PowerType.AC_3_PHASE,
        maxVoltage = 220,
        maxAmperage = 16,
        tariffIds = listOf("tariffId"),
        lastUpdated = timestamp,
        maxElectricPower = 7000,
        termsAndConditions = "termsAndConditions",
    )

    val coordinates = GeoLocation(latitude = "12.34", longitude = "-56.7890")
    val evse = Evse(
        uid = "uid",
        evseId = "evseId",
        status = Status.AVAILABLE,
        capabilities = listOf(Capability.RESERVABLE),
        connectors = listOf(connector),
        floorLevel = "floorLevel",
        physicalReference = "physicalReference",
        lastUpdated = timestamp,
        coordinates = coordinates,
        directions = listOf(displayText),
        parkingRestrictions = listOf(ParkingRestriction.CUSTOMERS),
        images = listOf(
            image,
        ),
        statusSchedule = listOf(
            StatusSchedule(
                periodBegin = timestamp,
                periodEnd = timestamp,
                status = Status.CHARGING,
            ),
        ),
    )

    val location = Location(
        countryCode = "FR",
        partyId = "ABC",
        id = "id",
        publish = true,
        publishAllowedTo = listOf(
            PublishTokenType(
                uid = "uid",
                type = TokenType.RFID,
                visualNumber = "visualNumber",
                issuer = "issuer",
                groupId = "groupId",
            ),
        ),
        name = "name",
        address = "address",
        city = "city",
        postalCode = "postalCode",
        state = "state",
        country = "country",
        coordinates = coordinates,
        relatedLocations = listOf(
            AdditionalGeoLocation(
                latitude = "12.34",
                longitude = "-56.7890",
                name = displayText,
            ),
        ),
        parkingType = ParkingType.PARKING_LOT,
        evses = listOf(
            evse,
        ),
        directions = listOf(
            displayText,
        ),
        operator = businessDetails,
        suboperator = businessDetails,
        owner = businessDetails,
        facilities = listOf(Facility.BIKE_SHARING),
        timeZone = "Europe/Paris",
        openingTimes = Hours(
            regularHours = listOf(RegularHours(weekday = 1, periodBegin = "00:00", periodEnd = "23:59")),
            twentyfourseven = false,
            exceptionalOpenings = listOf(
                exceptionalPeriod,
            ),
            exceptionalClosings = listOf(
                exceptionalPeriod,
            ),
        ),
        chargingWhenClosed = true,
        images = listOf(
            image,
        ),
        energyMix = EnergyMix(
            isGreenEnergy = true,
            energySources = listOf(
                EnergySource(
                    source = EnergySourceCategory.NUCLEAR,
                    percentage = BigDecimal(100),
                ),
            ),
            environImpact = listOf(
                EnvironmentalImpact(
                    category = EnvironmentalImpactCategory.NUCLEAR_WASTE,
                    amount = BigDecimal(100),
                ),
            ),
            supplierName = "supplierName",
            energyProductName = "energyProductName",
        ),
        lastUpdated = Instant.parse("2025-01-01T10:00:00.123Z"),
    )

    val token = Token(
        countryCode = "FR",
        partyId = "ABC",
        uid = "uid",
        type = TokenType.RFID,
        contractId = "contractId",
        visualNumber = "visualNumber",
        issuer = "issuer",
        groupId = "groupId",
        valid = true,
        whitelist = WhitelistType.ALLOWED,
        language = "language",
        defaultProfileType = ProfileType.GREEN,
        energyContract = EnergyContract(
            supplierName = "supplierName",
            contractId = "contractId",
        ),
        lastUpdated = timestamp,
    )

    val authorizationInfo = AuthorizationInfo(
        allowed = AllowedType.NOT_ALLOWED,
        token = token,
        location = LocationReferences(
            locationId = "locationId",
            evseUids = listOf("evseUid"),
        ),
        authorizationReference = "authorizationReference",
        info = displayText,
    )

    val tariff = Tariff(
        id = "id",
        type = TariffType.PROFILE_FAST,
        countryCode = "FR",
        partyId = "ABC",
        currency = "currency",
        tariffAltUrl = "https://example.com/tariffs",
        elements = listOf(
            TariffElement(
                priceComponents = listOf(
                    PriceComponent(
                        type = TariffDimensionType.PARKING_TIME,
                        price = BigDecimal(123),
                        vat = BigDecimal(456),
                        stepSize = 23,
                    ),
                ),
                restrictions = TariffRestrictions(
                    startTime = "08:00",
                    endTime = "20:00",
                    startDate = "2025-01-01",
                    endDate = "2025-12-31",
                    minKwh = BigDecimal("3.0"),
                    maxKwh = BigDecimal("22.0"),
                    minDuration = 300,
                    maxDuration = 7200,
                    dayOfWeek = listOf(DayOfWeek.SATURDAY),
                    reservation = ReservationRestrictionType.RESERVATION,
                    minPower = BigDecimal("100.0"),
                    maxPower = BigDecimal("200.0"),
                ),
            ),
        ),
        lastUpdated = timestamp,
        startDateTime = timestamp,
        endDateTime = timestamp.plusSeconds(3600),
        energyMix = EnergyMix(
            isGreenEnergy = true,
            energySources = listOf(
                EnergySource(
                    source = EnergySourceCategory.NUCLEAR,
                    percentage = BigDecimal(100),
                ),
            ),
            environImpact = listOf(
                EnvironmentalImpact(
                    category = EnvironmentalImpactCategory.NUCLEAR_WASTE,
                    amount = BigDecimal(100),
                ),
            ),
            supplierName = "supplierName",
            energyProductName = "energyProductName",
        ),
        minPrice = Price(
            exclVat = BigDecimal("10.00"),
            inclVat = BigDecimal("12.34"),
        ),
        maxPrice = Price(
            exclVat = BigDecimal("10.00"),
            inclVat = BigDecimal("12.34"),
        ),
        tariffAltText = listOf(displayText),
    )

    val cdrToken = CdrToken(
        countryCode = "countryCode",
        partyId = "partyId",
        uid = "uid",
        type = TokenType.AD_HOC_USER,
        contractId = "contractId",
    )

    val chargingPeriod = ChargingPeriod(
        startDateTime = timestamp,
        dimensions = listOf(
            CdrDimension(
                type = CdrDimensionType.ENERGY,
                volume = BigDecimal("15.123"),
            ),
        ),
    )

    val session = Session(
        currency = "currency",
        countryCode = "countryCode",
        partyId = "partyId",
        id = "id",
        startDateTime = timestamp,
        endDateTime = timestamp,
        kwh = BigDecimal("15.123"),
        cdrToken = cdrToken,
        authMethod = AuthMethod.COMMAND,
        authorizationReference = "12345",
        locationId = "LOC1",
        evseUid = "EVSE1",
        connectorId = "CONN1",
        meterId = "METER1",
        chargingPeriods = listOf(
            chargingPeriod,
        ),
        totalCost = Price(
            exclVat = BigDecimal("10.00"),
            inclVat = BigDecimal("12.34"),
        ),
        status = SessionStatusType.COMPLETED,
        lastUpdated = timestamp,
    )

    val price = Price(
        exclVat = BigDecimal("10.00"),
        inclVat = BigDecimal("12.00"),
    )

    val cdr = Cdr(
        countryCode = "countryCode",
        partyId = "partyId",
        id = "id",
        startDateTime = timestamp,
        endDateTime = timestamp,
        cdrToken = cdrToken,
        authMethod = AuthMethod.COMMAND,
        authorizationReference = "authorizationReference",
        meterId = "meterId",
        currency = "EUR",
        tariffs = listOf(tariff),
        chargingPeriods = listOf(
            chargingPeriod,
        ),
        totalCost = price,
        totalEnergy = BigDecimal("20.0"),
        totalTime = BigDecimal("3600"),
        totalParkingTime = BigDecimal("1800"),
        lastUpdated = timestamp,
        signedData = SignedData(
            encodingMethod = "encodingMethod",
            encodingMethodVersion = 23,
            publicKey = "publicKey",
            signedValues = listOf(
                SignedValue(
                    nature = "nature",
                    plainData = "plainData",
                    signedData = "signedData",
                ),
            ),
            url = "url",
        ),
        cdrLocation = CdrLocation(
            id = "id",
            name = "name",
            address = "address",
            city = "city",
            postalCode = "postalCode",
            state = "state",
            country = "country",
            coordinates = coordinates,
            evseUid = "evseUid",
            evseId = "evseId",
            connectorId = "connectorId",
            connectorStandard = ConnectorType.IEC_62196_T2,
            connectorFormat = ConnectorFormat.SOCKET,
            connectorPowerType = PowerType.AC_2_PHASE_SPLIT,
        ),
        credit = true,
        remark = "remark",
        sessionId = "sessionId",
        totalTimeCost = price,
        totalFixedCost = price,
        totalEnergyCost = price,
        totalParkingCost = price,
        creditReferenceId = "creditReferenceId",
        invoiceReferenceId = "invoiceReferenceId",
        totalReservationCost = price,
        homeChargingCompensation = true,
    )

    val chargingProfile = ChargingProfile(
        startDateTime = timestamp,
        duration = 12,
        chargingRateUnit = ChargingRateUnit.A,
        minChargingRate = BigDecimal(123),
        chargingProfilePeriod = listOf(
            ChargingProfilePeriod(
                startPeriod = 32,
                limit = BigDecimal(123),
            ),
        ),
    )
    val activeChargingProfileResult = ActiveChargingProfileResult(
        result = ChargingProfileResultType.REJECTED,
        profile = ActiveChargingProfile(
            startDateTime = timestamp,
            chargingProfile = chargingProfile,
        ),
    )

    val chargingProfileResponse = ChargingProfileResponse(
        result = ChargingProfileResponseType.UNKNOWN_SESSION,
        timeout = 12,
    )

    val chargingProfileResult = ChargingProfileResult(
        result = ChargingProfileResultType.ACCEPTED,
    )

    val clearProfileResult = ClearProfileResult(
        result = ChargingProfileResultType.UNKNOWN,
    )

    val setChargingProfile = SetChargingProfile(
        chargingProfile = chargingProfile,
        responseUrl = "responseUrl",
    )

    val cancelReservation = CancelReservation(
        responseUrl = "responseUrl",
        reservationId = "reservationId",
    )

    val commandResponse = CommandResponse(
        result = CommandResponseType.ACCEPTED,
        timeout = 314,
        message = listOf(displayText),
    )

    val commandResult = CommandResult(
        result = CommandResultType.REJECTED,
        message = listOf(displayText),
    )

    val reserveNow = ReserveNow(
        responseUrl = "responseUrl",
        reservationId = "reservationId",
        token = token,
        evseUid = "evseUid",
        expiryDate = timestamp,
        locationId = "locationId",
        authorizationReference = "authorizationReference",
    )

    val startSession = StartSession(
        responseUrl = "responseUrl",
        token = token,
        locationId = "locationId",
        evseUid = "evseUid",
        connectorId = "connectorId",
        authorizationReference = "authorizationReference",
    )

    val stopSession = StopSession(
        responseUrl = "responseUrl",
        sessionId = "sessionId",
    )

    val unlockConnector = UnlockConnector(
        responseUrl = "responseUrl",
        evseUid = "evseUid",
        connectorId = "connectorId",
        locationId = "locationId",
    )

    val credentials = Credentials(
        token = "token",
        url = "url",
        roles = listOf(
            CredentialRole(
                role = Role.CPO,
                businessDetails = businessDetails,
                partyId = "partyId",
                countryCode = "countryCode",
            ),
        ),
    )

    val versionDetails = VersionDetails(
        version = "version",
        endpoints = listOf(
            Endpoint(
                identifier = ModuleID.credentials,
                role = InterfaceRole.RECEIVER,
                url = "url",
            ),
        ),
    )

    fun <T> ocpiResponseBody(data: T) = OcpiResponseBody(
        statusCode = 1000,
        statusMessage = "message",
        timestamp = timestamp,
        data = data,
    )
}
