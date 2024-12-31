package br.com.creditas.credit_simulator.application.ports.`in`

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import java.time.LocalDate

interface CreditSimulatorPort {
    suspend operator fun invoke(
        dateOfBirth: LocalDate,
        creditSimulation: CreditSimulation
    ): CreditSimulation
}