package com.ardi.afarensis.dto.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PortValidator::class])
annotation class ValidPort(
    val message: String = "올바른 포트 번호가 아닙니다 (1-65535)",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class PortValidator : ConstraintValidator<ValidPort, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return false

        return try {
            val port = value.toInt()
            port in 1..65535
        } catch (e: NumberFormatException) {
            false
        }
    }
}


