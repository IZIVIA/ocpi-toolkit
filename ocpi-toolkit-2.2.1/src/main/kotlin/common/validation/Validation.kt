package common.validation

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
