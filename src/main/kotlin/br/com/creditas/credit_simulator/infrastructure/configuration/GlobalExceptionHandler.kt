package br.com.creditas.credit_simulator.infrastructure.configuration

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.zalando.problem.ProblemBuilder
import org.zalando.problem.Status
import org.zalando.problem.StatusType
import org.zalando.problem.spring.web.advice.ProblemHandling
import java.net.URI

@ControllerAdvice
class GlobalExceptionHandler : ProblemHandling {
    private val businessExceptions: List<Class<out RuntimeException>> = listOf(
        // adicionar exceções que desejamos retornar com status HTTP 422
    )

    override fun prepare(throwable: Throwable, status: StatusType, type: URI): ProblemBuilder {
        val builder: ProblemBuilder = super.prepare(throwable, status, type)

        return when (throwable) {
            is  ResponseStatusException -> builder.withDetail(throwable.reason)
            else -> handleBusinessExceptions(throwable, status, builder)
        }
    }

    private fun handleBusinessExceptions(
        throwable: Throwable,
        status: StatusType,
        builder: ProblemBuilder
    ): ProblemBuilder {
        return if (businessExceptions.any { it.isInstance(throwable) }) {
            builder
                .withStatus(Status.UNPROCESSABLE_ENTITY)
                .withTitle(HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase)
                .withDetail(throwable.message ?: "Business rule violation")
        } else {
            handleUnexpectedException(status, builder)
        }
    }

    private fun handleUnexpectedException(status: StatusType, builder: ProblemBuilder): ProblemBuilder {
        return if (status.statusCode == 500) {
            val unexpectedError: StatusType = Status.INTERNAL_SERVER_ERROR
            return builder
                .withStatus(unexpectedError)
                .withTitle(unexpectedError.reasonPhrase)
                .withDetail("An unexpected error occurred. Please contact the system administrator.")
        } else {
            builder
        }
    }
}
