package br.com.creditas.credit_simulator.application.ports.out

import br.com.creditas.credit_simulator.application.domain.CreditSimulation

interface CreditSimulationRepositoryPort {
    fun save(creditSimulation: CreditSimulation): CreditSimulation
}