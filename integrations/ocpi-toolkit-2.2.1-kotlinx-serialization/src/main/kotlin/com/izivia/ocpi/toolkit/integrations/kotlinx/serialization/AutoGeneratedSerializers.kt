package com.izivia.ocpi.toolkit.integrations.kotlinx.serialization

import com.izivia.ocpi.toolkit.annotations.CustomClassSerializer
import com.izivia.ocpi.toolkit.annotations.CustomFieldSerializer
import com.izivia.ocpi.toolkit.annotations.FieldSerializer
import com.izivia.ocpi.toolkit.annotations.GenerateSerializers
import com.izivia.ocpi.toolkit.integrations.kotlinx.serialization.serializers.*
import com.izivia.ocpi.toolkit.modules.cdr.domain.*
import com.izivia.ocpi.toolkit.modules.chargingProfiles.domain.*
import com.izivia.ocpi.toolkit.modules.commands.domain.*
import com.izivia.ocpi.toolkit.modules.credentials.domain.CredentialRole
import com.izivia.ocpi.toolkit.modules.credentials.domain.Credentials
import com.izivia.ocpi.toolkit.modules.locations.domain.*
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferences
import com.izivia.ocpi.toolkit.modules.sessions.domain.ChargingPreferencesPartial
import com.izivia.ocpi.toolkit.modules.sessions.domain.Session
import com.izivia.ocpi.toolkit.modules.sessions.domain.SessionPartial
import com.izivia.ocpi.toolkit.modules.tariff.domain.*
import com.izivia.ocpi.toolkit.modules.tokens.domain.*
import com.izivia.ocpi.toolkit.modules.types.DisplayText
import com.izivia.ocpi.toolkit.modules.types.DisplayTextPartial
import com.izivia.ocpi.toolkit.modules.types.Price
import com.izivia.ocpi.toolkit.modules.types.PricePartial
import com.izivia.ocpi.toolkit.modules.versions.domain.Endpoint
import com.izivia.ocpi.toolkit.modules.versions.domain.Version
import com.izivia.ocpi.toolkit.modules.versions.domain.VersionDetails
import java.math.BigDecimal
import java.time.Instant

@GenerateSerializers(
    classes = [
        // CDR
        Cdr::class,
        CdrPartial::class,
        CdrDimension::class,
        CdrDimensionPartial::class,
        CdrLocation::class,
        CdrToken::class,
        CdrTokenPartial::class,
        ChargingPeriod::class,
        ChargingPeriodPartial::class,
        SignedData::class,
        SignedDataPartial::class,
        SignedValue::class,
        SignedValuePartial::class,

        // ChargingProfiles
        ActiveChargingProfile::class,
        ActiveChargingProfileResult::class,
        ChargingProfile::class,
        ChargingProfilePeriod::class,
        ChargingProfileResponse::class,
        ChargingProfileResult::class,
        ClearProfileResult::class,
        SetChargingProfile::class,

        // Commands
        CancelReservation::class,
        CommandResponse::class,
        CommandResult::class,
        ReserveNow::class,
        StartSession::class,
        StopSession::class,
        UnlockConnector::class,

        // Credentials
        Credentials::class,
        CredentialRole::class,

        // Sessions
        ChargingPreferences::class,
        ChargingPreferencesPartial::class,
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
        PublishTokenType::class,
        PublishTokenTypePartial::class,
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
        EnergyContract::class,
        EnergyContractPartial::class,
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
    customFieldSerializers = [
        CustomFieldSerializer(
            targetClass = Connector::class,
            fieldSerializer = [
                FieldSerializer("tariffIds", SkipNullsListSerializer::class),
            ],
        ),
    ],
    excludedSerializersFromModule = [],
)
class AutoGeneratedSerializers
