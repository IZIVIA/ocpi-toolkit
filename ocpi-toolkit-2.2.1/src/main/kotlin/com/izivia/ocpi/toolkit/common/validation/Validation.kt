package com.izivia.ocpi.toolkit.common.validation

import org.valiktor.ConstraintViolation
import org.valiktor.ConstraintViolationException
import org.valiktor.DefaultConstraintViolation
import org.valiktor.constraints.Greater
import org.valiktor.constraints.Less
import java.time.Instant

data class ValidationContext(
    val violations: MutableSet<ConstraintViolation> = mutableSetOf(),
)

fun <T> validate(fn: ValidationContext.() -> T) = with(ValidationContext()) {
    fn()

    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }
}

fun ValidationContext.validateLength(property: String, value: String, maxLength: Int) {
    if (value.length > maxLength) {
        violations.add(
            DefaultConstraintViolation(
                property = property,
                value = value,
                constraint = MaxLengthContraint(maxLength),
            ),
        )
    }
}

fun ValidationContext.validateDates(fromProperty: String, fromValue: Instant, toProperty: String, toValue: Instant) {
    if (fromValue.isAfter(toValue)) {
        violations.add(
            DefaultConstraintViolation(
                property = fromProperty,
                value = fromValue,
                constraint = IsAfterContraint(toProperty, toValue),
            ),
        )
    }
}

fun ValidationContext.validateInt(property: String, value: Int, min: Int?, max: Int?) {
    if (min != null && value < min) {
        violations.add(
            DefaultConstraintViolation(
                property = property,
                value = value,
                constraint = Less(min),
            ),
        )
    } else if (max != null && value > max) {
        violations.add(
            DefaultConstraintViolation(
                property = property,
                value = value,
                constraint = Greater(max),
            ),
        )
    }
}

fun ValidationContext.validateSame(property: String, value: String, reference: String) {
    if (value != reference) {
        violations.add(
            DefaultConstraintViolation(
                property = property,
                value = value,
                constraint = SameStringValueConstraint(reference),
            ),
        )
    }
}
