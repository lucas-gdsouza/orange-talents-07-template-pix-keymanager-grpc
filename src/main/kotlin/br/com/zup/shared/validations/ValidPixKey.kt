package br.com.zup.shared.validations

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])
annotation class ValidPixKey(
    val message: String = "CHAVE INVÁLIDA",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)