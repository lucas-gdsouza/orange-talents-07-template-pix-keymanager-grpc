package br.com.zup.shared.validations

import br.com.zup.grpc.novachave.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton

@Singleton
class ValidPixKeyValidator : ConstraintValidator<ValidPixKey, NovaChavePix> {
    override fun isValid(value: NovaChavePix?,
                         annotationMetadata: AnnotationValue<ValidPixKey>,
                         context: ConstraintValidatorContext): Boolean {
        if (value?.tipoDeChave == null) {
            return true
        }
        return value.tipoDeChave.valida(value.chave)
    }
}