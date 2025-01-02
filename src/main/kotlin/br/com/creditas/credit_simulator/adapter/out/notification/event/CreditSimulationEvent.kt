package br.com.creditas.credit_simulator.adapter.out.notification.event

import br.com.creditas.credit_simulator.application.domain.CreditSimulation
import br.com.creditas.credit_simulator.application.domain.CreditSimulationStatus
import br.com.creditas.credit_simulator.application.domain.InterestRate.InterestRateType
import br.com.creditas.credit_simulator.application.domain.InterestRate.MarketIndexName
import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class CreditSimulationEvent private constructor(
    val simulationId: UUID,
    val presentValue: BigDecimal?,
    val numberOfPayments: Int?,
    val interestRate: InterestRate?,
    val monthlyPayment: BigDecimal?,
    val email: String?,
    val status: EventStatus
) {
    enum class EventStatus {
        SUCCESS, ERROR
    }

    class InterestRate(
        var spread: BigDecimal? = null,
        var annualInterestRate: BigDecimal,
        val interestRateType: InterestRateType,
        val marketIndexName: MarketIndexName? = null,
        var marketIndexAnnualInterestRate: BigDecimal? = null,
    )

    constructor(
        creditSimulation: CreditSimulation
    ) : this(
        status = getStatus(creditSimulation.status!!),
        simulationId = creditSimulation.simulationId,
        email = creditSimulation.email,
        presentValue = creditSimulation.presentValue,
        numberOfPayments = creditSimulation.numberOfPayments,
        interestRate = fromInterestRate(creditSimulation.interestRate!!),
        monthlyPayment = creditSimulation.monthlyPayment
    )

    companion object{
        fun getStatus(creditSimulationStatus: CreditSimulationStatus) : EventStatus {
            return when(creditSimulationStatus) {
                CreditSimulationStatus.SUCCESS -> EventStatus.SUCCESS
                CreditSimulationStatus.ERROR -> EventStatus.ERROR
            }
        }

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