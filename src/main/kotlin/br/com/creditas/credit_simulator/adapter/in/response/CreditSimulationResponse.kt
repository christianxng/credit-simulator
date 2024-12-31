package br.com.creditas.credit_simulator.adapter.`in`.response

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import java.math.BigDecimal
import java.util.*

data class CreditSimulationResponse(
    val simulationId: UUID,
    val presentValue: BigDecimal,
    val numberOfPayments: Int,
    val annualInterestRate: BigDecimal,
    var monthlyPayment: BigDecimal?
) {

    constructor(creditSimulation: CreditSimulation) : this(
        simulationId = creditSimulation.simulationId,
        presentValue = creditSimulation.presentValue,
        numberOfPayments = creditSimulation.numberOfPayments,
        annualInterestRate = creditSimulation.annualInterestRate!!,
        monthlyPayment = creditSimulation.monthlyPayment,
    )
}
