package br.com.creditas.credit_simulator.adapter.out.repository

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.ports.out.CreditSimulationRepositoryPort
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CreditSimulationRepositoryAdapter(
    private val creditSimulationRepository: CreditSimulationRepository
): CreditSimulationRepositoryPort {

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Transactional
    override fun save(creditSimulation: CreditSimulation): CreditSimulation {

        logger.info("Saving simulation to database: ${creditSimulation.simulationId}")
        return runCatching {
            val creditSimulationEntity = CreditSimulationEntity.toEntity(creditSimulation)
            creditSimulationRepository.save(creditSimulationEntity)
            logger.info("Simulation saved in the database: ${creditSimulation.simulationId}")
            creditSimulation
        }.onFailure {
            ex ->
            logger.error("Error saving simulation to database: ${creditSimulation.simulationId}", ex)
            throw ex
        }.getOrThrow()
    }
}