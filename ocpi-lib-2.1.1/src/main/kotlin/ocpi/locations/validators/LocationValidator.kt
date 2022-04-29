package ocpi.locations.validators

import ocpi.locations.domain.*
import org.valiktor.functions.isValid
import org.valiktor.validate

fun Location.validate(): Location = validate(this) {
    validate(Location::id).isValid { it.length <= 39 }
    validate(Location::name).isValid { it.length <= 255 }
    validate(Location::address).isValid { it.length <= 45 }
    validate(Location::city).isValid { it.length <= 45 }
    validate(Location::postal_code).isValid { it.length <= 10 }

    // ISO 3166-1 alpha-3 code
    validate(Location::country).isValid { it.length <= 3 }
    validate(Location::country).isValid { it.uppercase() == it }

    // TODO
}

fun Evse.validate(): Evse = validate(this) {
    // TODO
}

fun Connector.validate(): Connector = validate(this) {
    // TODO
}

fun LocationPartial.validate(): LocationPartial = validate(this) {
    // TODO
}

fun EvsePartial.validate(): EvsePartial = validate(this) {
    // TODO
}

fun ConnectorPartial.validate(): ConnectorPartial = validate(this) {
    // TODO
}