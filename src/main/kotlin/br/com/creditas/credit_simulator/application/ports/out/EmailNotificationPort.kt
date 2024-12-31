package br.com.creditas.credit_simulator.application.ports.out

import br.com.creditas.credit_simulator.application.domain.CreditSimulation

interface EmailNotificationPort {
    operator fun invoke(creditSimulation: CreditSimulation)
}