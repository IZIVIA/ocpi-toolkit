package common

import ocpi.credentials.repositories.PlatformRepository
import org.valiktor.ConstraintViolation
import org.valiktor.ConstraintViolationException
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.constraints.Less
import java.time.Instant

data class ValidationContext(
    val violations: MutableSet<ConstraintViolation> = mutableSetOf()
)

fun <T> validate(fn: ValidationContext.() -> T) = with(ValidationContext()) {
    fn()

    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }
}

/**
 * TODO: is it the good behaviour given:
 * - tokenA: Valid in receiver context, during sender registration (only for sender -> receiver calls)
 * - tokenB: Valid in sender context, during sender registration (only for receiver -> sender calls)
 * - tokenC: Valid when the sender is registered with the receiver (only for sender -> receiver)
 *
 * @throws OcpiClientInvalidParametersException if the token is invalid, otherwise does nothing
 */
fun validateToken(platformRepository: PlatformRepository, token: String) {
    if (platformRepository.getPlatformByTokenA(token) == null &&
        platformRepository.getPlatformByTokenB(token) == null &&
        platformRepository.getPlatformByTokenC(token) == null) {

        throw OcpiClientInvalidParametersException("Invalid token: $token")
    }
}

fun ValidationContext.validateLength(propertyName: String, property: String, maxLength: Int) {
    if (property.length > maxLength) {
        violations.add(
            DefaultConstraintViolation(
                property = property,
                constraint = Greater(maxLength)
            )
        )
    }
}

fun ValidationContext.validateDates(fromPropertyName: String, from: Instant, toPropertyName: String, to: Instant) {
    if (from.isAfter(to)) {
        violations.add(
            DefaultConstraintViolation(
                property = "from",
                constraint = Greater(to)
            )
        )
    }
}

fun ValidationContext.validateInt(propertyName: String, property: Int, min: Int?, max: Int?) {
    if (min != null && property < min) {
        violations.add(
            DefaultConstraintViolation(
                property = "value",
                constraint = Less(min)
            )
        )
    } else if (max != null && property > max) {
        violations.add(
            DefaultConstraintViolation(
                property = "value",
                constraint = Greater(min)
            )
        )
    }
}
