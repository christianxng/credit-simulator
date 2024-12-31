package br.com.creditas.credit_simulator.infrastructure.utils

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class CustomMetrics(
    val registry: MeterRegistry
) {
    companion object {
        const val PREFIX = "credit_simulator_"
    }

    fun sendEmailError() {
        Counter.builder("${PREFIX}send_email_error")
            .description("Custom metric for counting failed email deliveries")
            .register(registry)
            .count()
    }

    fun sendEventError() {
        Counter.builder("${PREFIX}send_event_error")
            .description("Custom metric for counting failed event deliveries")
            .register(registry)
            .count()
    }
}