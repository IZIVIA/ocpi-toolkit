package com.izivia.ocpi.toolkit211.integrations.kotlinx.serialization

import com.izivia.ocpi.toolkit211.integrations.kotlinx.serialization.serializers.*
import com.izivia.ocpi.toolkit211.modules.cdr.domain.*
import com.izivia.ocpi.toolkit211.modules.commands.domain.*
import com.izivia.ocpi.toolkit211.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit211.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit211.modules.locations.domain.*
import com.izivia.ocpi.toolkit211.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit211.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit211.modules.tariff.domain.*
import com.izivia.ocpi.toolkit211.modules.tokens.domain.*
import com.izivia.ocpi.toolkit211.modules.types.DisplayText
import com.izivia.ocpi.toolkit211.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit211.modules.types.Price
import com.izivia.ocpi.toolkit211.modules.types.PricePartial
import com.izivia.ocpi.toolkit211.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit211.modules.versions.domain.Version
import com.izivia.ocpi.toolkit211.modules.versions.domain.VersionDetails
import dev.gallon.konstruct.annotations.CustomClassSerializer
import dev.gallon.konstruct.annotations.GenerateSerializers
import java.math.BigDecimal
import java.time.Instant

@GenerateSerializers(
    classes = [
        // CDR
        Cdr::class,
        CdrPartial::class,
        CdrDimension::class,
        CdrDimensionPartial::class,
        ChargingPeriod::class,
        ChargingPeriodPartial::class,

        // Commands
        CommandResponse::class,
        ReserveNow::class,
        StartSession::class,
        StopSession::class,
        UnlockConnector::class,

        // Credentials
        Credentials::class,
        CredentialRole::class,

        // Sessions
        Session::class,
        SessionPartial::class,

        // Locations
        AdditionalGeoLocation::class,
        AdditionalGeoLocationPartial::class,
        BusinessDetails::class,
        BusinessDetailsPartial::class,
        Connector::class,
        ConnectorPartial::class,
        EnergyMix::class,
        EnergyMixPartial::class,
        EnergySource::class,
        EnergySourcePartial::class,
        EnvironmentalImpact::class,
        EnvironmentalImpactPartial::class,
        Evse::class,
        EvsePartial::class,
        ExceptionalPeriod::class,
        ExceptionalPeriodPartial::class,
        GeoLocation::class,
        GeoLocationPartial::class,
        Hours::class,
        HoursPartial::class,
        Image::class,
        ImagePartial::class,
        Location::class,
        LocationPartial::class,
        RegularHours::class,
        RegularHoursPartial::class,
        StatusSchedule::class,
        StatusSchedulePartial::class,

        // Tariff
        PriceComponent::class,
        PriceComponentPartial::class,
        Tariff::class,
        TariffPartial::class,
        TariffElement::class,
        TariffElementPartial::class,
        TariffRestrictions::class,
        TariffRestrictionsPartial::class,

        // Token
        AuthorizationInfo::class,
        AuthorizationInfoPartial::class,
        LocationReferences::class,
        LocationReferencesPartial::class,
        Token::class,
        TokenPartial::class,

        // Version
        Endpoint::class,
        Version::class,
        VersionDetails::class,

        // Types
        DisplayText::class,
        DisplayTextPartial::class,
        Price::class,
        PricePartial::class,
    ],
    customClassSerializers = [
        // Built-in types
        CustomClassSerializer(targetClass = BigDecimal::class, serializer = BigDecimalSerializer::class),
        CustomClassSerializer(targetClass = Instant::class, serializer = InstantSerializer::class),

        // Enums
        CustomClassSerializer(targetClass = Capability::class, serializer = CapabilitySerializer::class),
        CustomClassSerializer(targetClass = ConnectorType::class, serializer = ConnectorTypeSerializer::class),
        CustomClassSerializer(targetClass = Facility::class, serializer = FacilitySerializer::class),
        CustomClassSerializer(targetClass = ImageCategory::class, serializer = ImageCategorySerializer::class),
        CustomClassSerializer(
            targetClass = ParkingRestriction::class,
            serializer = ParkingRestrictionSerializer::class,
        ),
        CustomClassSerializer(targetClass = ParkingType::class, serializer = ParkingTypeSerializer::class),
    ],
    customFieldSerializers = [],
    excludedSerializersFromModule = [],
)
class OcpiSerializers
