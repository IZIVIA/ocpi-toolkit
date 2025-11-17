package com.izivia.ocpi.toolkit.integrations.jackson.mixins

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class CapacityMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class ConnectorTypeMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class FacilityMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class ImageCategoryMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class ParkingRestrictionMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class ParkingTypeMixin {
    @JsonEnumDefaultValue
    OTHER,
}

enum class ConnectionStatusMixin {
    @JsonEnumDefaultValue
    OTHER,
}
