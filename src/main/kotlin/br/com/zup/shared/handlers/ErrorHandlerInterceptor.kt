package br.com.zup.shared.handlers

import br.com.zup.shared.exceptions.ChavePixExistenteException
import br.com.zup.shared.exceptions.ClienteNaoExistenteException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.context.MessageSource
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.naming.ServiceUnavailableException
import javax.persistence.PersistenceException
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ErrorHandlerInterceptor() : MethodInterceptor<Any, Any> {

    @Inject
    lateinit var messageSource: MessageSource

    override fun intercept(context: MethodInvocationContext<Any, Any>?): Any? {

        try {
            return context?.proceed()
        } catch (ex: Exception) {
            val responseObserver = context!!.parameterValues[1] as StreamObserver<*>
            val status: Status = fromException(ex)
            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }

    private fun fromException(ex: Exception): Status {
        return when (ex) {

            is ServiceUnavailableException -> status(Status.UNAVAILABLE, ex)
            is IllegalStateException -> status(Status.FAILED_PRECONDITION, ex)
            is ConstraintViolationException -> status(Status.INVALID_ARGUMENT, ex)
            is PersistenceException -> status(Status.INVALID_ARGUMENT, ex)
            is ChavePixExistenteException -> status(Status.ALREADY_EXISTS, ex)
            is ClienteNaoExistenteException -> status(Status.FAILED_PRECONDITION, ex)

            else -> Status.UNKNOWN.withCause(ex).withDescription("ERRO_INTERNO")
        }
    }

    private fun status(status: Status, ex: Exception): Status {
        return Status.fromCode(status.code).withCause(ex).withDescription(ex.message)
    }
}