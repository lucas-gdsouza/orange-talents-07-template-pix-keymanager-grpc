package br.com.zup.shared.handlers

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.FILE,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)

@Around
@Type(ErrorHandlerInterceptor::class)
annotation class ErrorHandler