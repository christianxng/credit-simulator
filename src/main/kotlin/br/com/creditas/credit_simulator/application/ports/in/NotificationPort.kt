package br.com.creditas.credit_simulator.application.ports.`in`

import br.com.creditas.credit_simulator.application.domain.CreditSimulation

interface NotificationPort {
    suspend operator fun invoke(creditSimulation: CreditSimulation)
}