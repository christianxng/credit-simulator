package br.com.creditas.credit_simulator.adapter.out.repository

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.domain.CreditSimulationStatus
import br.com.creditas.credit_simulator.application.domain.InterestRate.InterestRateType
import br.com.creditas.credit_simulator.application.domain.InterestRate.MarketIndexName
import jakarta.persistence.*
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

    @Column(name = "monthly_payment", nullable = true, precision = 19, scale = 2)
    val monthlyPayment: BigDecimal?,

    @Column(nullable = false) @Email
    val email: String,

    @Column(nullable = false)
    private var status: String,

    @Embedded
    val interestRate: InterestRate
) {

    companion object {

        fun toEntity(creditSimulation: CreditSimulation): CreditSimulationEntity =
            CreditSimulationEntity(
                simulationId = creditSimulation.simulationId,
                presentValue = creditSimulation.presentValue,
                numberOfPayments = creditSimulation.numberOfPayments,
                interestRate = interestRateToEntity(creditSimulation.interestRate!!),
                monthlyPayment = creditSimulation.monthlyPayment,
                email = creditSimulation.email,
                status = if (creditSimulation.isSimulationWithError())
                    CreditSimulationStatus.ERROR.name
                else CreditSimulationStatus.SUCCESS.name
            )

        private fun interestRateToEntity(
            interestRate: br.com.creditas.credit_simulator.application.domain.InterestRate
        ): InterestRate {
            return InterestRate(
                spread = interestRate.spread,
                annualInterestRate = interestRate.annualInterestRate,
                interestRateType = interestRate.interestRateType,
                marketIndexName = interestRate.marketIndexName,
                marketIndexAnnualInterestRate = interestRate.marketIndexAnnualInterestRate,
            )
        }
    }

    @Embeddable
    class InterestRate(
        @Column(name = "spread", nullable = true, precision = 19, scale = 2)
        var spread: BigDecimal? = null,
        @Column(name = "annual_interest_rate", nullable = false, precision = 19, scale = 2)
        var annualInterestRate: BigDecimal,
        @Enumerated(EnumType.STRING)
        @Column(name = "interest_rate_type", nullable = false)
        val interestRateType: InterestRateType,
        @Enumerated(EnumType.STRING)
        @Column(name = "market_index_name", nullable = true)
        val marketIndexName: MarketIndexName? = null,
        @Column(name = "market_index_annual_interest_rate", nullable = true, precision = 19, scale = 2)
        var marketIndexAnnualInterestRate: BigDecimal? = null
    )
}
