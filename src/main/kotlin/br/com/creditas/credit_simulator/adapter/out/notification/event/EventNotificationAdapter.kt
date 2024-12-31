package br.com.creditas.credit_simulator.adapter.out.notification.event

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.ports.out.EventNotificationPort
import br.com.creditas.credit_simulator.infrastructure.utils.CustomMetrics
import br.com.creditas.credit_simulator.infrastructure.utils.JsonMapper.toJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component


@Component
class EventNotificationAdapter(
    private val customMetrics: CustomMetrics,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    @Value("\${spring.kafka.template.default-topic}") val topic: String
) : EventNotificationPort {

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun invoke(creditSimulation: CreditSimulation) {
        logger.info("Sending event notification for simulation ${creditSimulation.simulationId}")

        runCatching {
            val creditSimulationEvent = CreditSimulationEvent(
                creditSimulation = creditSimulation
            )
            kafkaTemplate.send(topic, toJson(creditSimulationEvent))
            logger.info("Event notification sent successfully for simulation ${creditSimulation.simulationId}")
        }.onFailure { ex ->
            customMetrics.sendEventError()
            logger.error("Error sending event notification for simulation ${creditSimulation.simulationId}", ex)
            throw ex
        }
    }
}