package br.com.creditas.credit_simulator.application.service

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.ports.`in`.NotificationPort
import br.com.creditas.credit_simulator.application.ports.out.EmailNotificationPort
import br.com.creditas.credit_simulator.application.ports.out.EventNotificationPort
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val eventNotificationPort: EventNotificationPort,
    private val emailNotificationPort: EmailNotificationPort
) : NotificationPort {

    override suspend operator fun invoke(creditSimulation: CreditSimulation) {
        coroutineScope {
            launch { eventNotificationPort(creditSimulation) }
            if (creditSimulation.isSimulationWithSuccess()) {
                launch { emailNotificationPort(creditSimulation) }
            }
        }
    }
}