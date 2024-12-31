package br.com.creditas.credit_simulator.application.service

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.ports.`in`.CreditSimulatorPort
import br.com.creditas.credit_simulator.application.ports.`in`.InterestRatePort
import br.com.creditas.credit_simulator.application.ports.`in`.NotificationPort
import br.com.creditas.credit_simulator.application.ports.out.CreditSimulationRepositoryPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class CreditSimulatorService(
    private val interestRatePort: InterestRatePort,
    private val notificationPort: NotificationPort,
    private val creditSimulationRepositoryPort: CreditSimulationRepositoryPort
) : CreditSimulatorPort {

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun invoke(dateOfBirth: LocalDate, creditSimulation: CreditSimulation): CreditSimulation {

        return runCatching {
            logger.info("Starting PMT calculation for simulation ${creditSimulation.simulationId}")

            val age = dateOfBirth.calculateAge()
            creditSimulation.process(interestRatePort(age))
            logger.info("Success when performing PMT calculation for simulation ${creditSimulation.simulationId}")

            creditSimulationRepositoryPort.save(creditSimulation)
            notificationPort(creditSimulation)
            creditSimulation
        }.onFailure { ex ->
            logger.error("Error performing PMT calculation for simulation ${creditSimulation.simulationId}", ex)
            throw ex
        }.getOrThrow()
    }

    private fun LocalDate.calculateAge(): Int =
        Period.between(this, LocalDate.now()).years
}