package br.com.creditas.credit_simulator.adapter.out.notification.email

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.ports.out.EmailNotificationPort
import br.com.creditas.credit_simulator.infrastructure.utils.Constants.EMAIL_SUBJECT
import br.com.creditas.credit_simulator.infrastructure.utils.CustomMetrics
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component


@Component
class EmailNotificationAdapter(
    private val customMetrics: CustomMetrics,
    private val mailSender: MailSender,
    @Value("\${amazon.ses.sender-email}") val senderEmail: String
) : EmailNotificationPort {

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun invoke(creditSimulation: CreditSimulation) {
        logger.info("Sending email for simulation ${creditSimulation.simulationId}")

        runCatching {
            val creditSimulationEmail = CreditSimulationEmail(creditSimulation = creditSimulation)

            val simpleMailMessage = SimpleMailMessage()
            simpleMailMessage.from = senderEmail
            simpleMailMessage.setTo(creditSimulationEmail.email)
            simpleMailMessage.subject = EMAIL_SUBJECT
            simpleMailMessage.text = creditSimulationEmail.buildEmail()

            mailSender.send(simpleMailMessage)
            logger.info("Email sent successfully for simulation ${creditSimulation.simulationId}")
        }.onFailure { ex ->
            customMetrics.sendEmailError()
            logger.info("Error sending email for simulation ${creditSimulation.simulationId}. Ex: $ex")
        }
    }
}