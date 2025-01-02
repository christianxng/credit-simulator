package br.com.creditas.credit_simulator.adapter.`in`.response

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.domain.InterestRate.InterestRateType
import br.com.creditas.credit_simulator.application.domain.InterestRate.MarketIndexName
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreditSimulationResponse(
    val simulationId: UUID,
    val presentValue: BigDecimal,
    val numberOfPayments: Int,
    val interestRate: InterestRate,
    var monthlyPayment: BigDecimal?
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class InterestRate(
        val interestRateType: InterestRateType,
        val marketIndexName: MarketIndexName? = null,
        var marketIndexAnnualInterestRate: BigDecimal? = null,
        var spread: BigDecimal? = null,
        var annualInterestRate: BigDecimal,
    )

    constructor(creditSimulation: CreditSimulation) : this(
        simulationId = creditSimulation.simulationId,
        presentValue = creditSimulation.presentValue,
        numberOfPayments = creditSimulation.numberOfPayments,
        interestRate = fromInterestRate(creditSimulation.interestRate!!),
        monthlyPayment = creditSimulation.monthlyPayment,
    )

    companion object {
        private fun fromInterestRate(
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
}
