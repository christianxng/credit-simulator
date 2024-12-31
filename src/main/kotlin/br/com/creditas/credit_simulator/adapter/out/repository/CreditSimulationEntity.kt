package br.com.creditas.credit_simulator.adapter.out.repository

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.domain.CreditSimulationStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "credit_simulations")
class CreditSimulationEntity(
    @Id
    val simulationId: UUID? = null,

    @Column(name = "present_value", nullable = false, precision = 19, scale = 2)
    val presentValue: BigDecimal,

    @Column(name = "number_of_payments", nullable = false)
    val numberOfPayments: Int,

    @Column(name = "annual_interest_rate", nullable = false, precision = 19, scale = 4)
    val annualInterestRate: BigDecimal,

    @Column(name = "monthly_payment", nullable = true, precision = 19, scale = 2)
    val monthlyPayment: BigDecimal?,

    @Column(nullable = false) @Email
    val email: String,

    @Column(nullable = false)
    private var status: String
) {

    companion object {

        fun toEntity(creditSimulation: CreditSimulation): CreditSimulationEntity =
            CreditSimulationEntity(
                simulationId = creditSimulation.simulationId,
                presentValue = creditSimulation.presentValue,
                numberOfPayments = creditSimulation.numberOfPayments,
                annualInterestRate = creditSimulation.annualInterestRate!!,
                monthlyPayment = creditSimulation.monthlyPayment,
                email = creditSimulation.email,
                status = if (creditSimulation.isSimulationWithError())
                    CreditSimulationStatus.ERROR.name
                else CreditSimulationStatus.SUCCESS.name
            )
    }
}