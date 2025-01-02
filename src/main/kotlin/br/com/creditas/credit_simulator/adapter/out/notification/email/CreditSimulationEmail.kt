package br.com.creditas.credit_simulator.adapter.out.notification.email

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class CreditSimulationEmail private constructor(
    val simulationId: UUID?,
    private val presentValue: BigDecimal,
    private val numberOfPayments: Int,
    private val annualInterestRate: BigDecimal,
    private val monthlyPayment: BigDecimal,
    val email: String,
) {

    constructor(
        creditSimulation: CreditSimulation
    ) : this(
        email = creditSimulation.email,
        simulationId = creditSimulation.simulationId,
        presentValue = creditSimulation.presentValue,
        numberOfPayments = creditSimulation.numberOfPayments,
        annualInterestRate = creditSimulation.interestRate!!.annualInterestRate,
        monthlyPayment = creditSimulation.monthlyPayment!!
    )

    fun buildEmail(): String {
        return """
        Credit Simulation - ID: $simulationId
        
        Dear Customer,
        
        Below are the details of your credit simulation:
        
        Requested amount: ${presentValue.setScale(2, RoundingMode.HALF_EVEN)}
        Number of installments: $numberOfPayments
        Annual interest rate: ${annualInterestRate.setScale(2, RoundingMode.HALF_EVEN)}%
        Monthly installment amount: ${monthlyPayment.setScale(2, RoundingMode.HALF_EVEN)}
        
       
        Best regards,
        Credit Team
    """.trimIndent()
    }
}