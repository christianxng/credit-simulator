package br.com.creditas.credit_simulator.adapter.out.notification.event

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.domain.CreditSimulationStatus
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreditSimulationEvent private constructor(
    val simulationId: UUID,
    val presentValue: BigDecimal?,
    val numberOfPayments: Int?,
    val annualInterestRate: BigDecimal?,
    val monthlyPayment: BigDecimal?,
    val email: String?,
    val status: EventStatus
) {
    enum class EventStatus {
        SUCCESS, ERROR
    }

    constructor(
        creditSimulation: CreditSimulation
    ) : this(
        status = getStatus(creditSimulation.status!!),
        simulationId = creditSimulation.simulationId,
        email = creditSimulation.email,
        presentValue = creditSimulation.presentValue,
        numberOfPayments = creditSimulation.numberOfPayments,
        annualInterestRate = creditSimulation.annualInterestRate,
        monthlyPayment = creditSimulation.monthlyPayment
    )

    companion object{
        fun getStatus(creditSimulationStatus: CreditSimulationStatus) : EventStatus {
            return when(creditSimulationStatus) {
                CreditSimulationStatus.SUCCESS -> EventStatus.SUCCESS
                CreditSimulationStatus.ERROR -> EventStatus.ERROR
            }
        }
    }
}