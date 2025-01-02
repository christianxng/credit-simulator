package br.com.creditas.credit_simulator.application.service

import br.com.creditas.credit_simulator.application.domain.InterestRate
import br.com.creditas.credit_simulator.application.ports.`in`.InterestRatePort
import br.com.creditas.credit_simulator.infrastructure.configuration.CreditSimulatorProperties
import br.com.creditas.credit_simulator.infrastructure.exceptions.InvalidMarketIndex
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class InterestRateService(
    private val creditSimulatorProperties: CreditSimulatorProperties
) : InterestRatePort {

    override fun invoke(yearsOld: Int): InterestRate {
        return when (creditSimulatorProperties.interestRateType) {
            CreditSimulatorProperties.InterestRateType.FIXED -> calculateFixedRate(yearsOld)
            CreditSimulatorProperties.InterestRateType.VARIABLE -> calculateVariableRate()
        }
    }

    private fun calculateFixedRate(yearsOld: Int): InterestRate {
        val annualInterestRate = when {
            yearsOld < 26 -> BigDecimal("0.05")
            yearsOld in 26..40 -> BigDecimal("0.03")
            yearsOld in 41..60 -> BigDecimal("0.02")
            else -> BigDecimal("0.04")
        }
        return InterestRate(
            annualInterestRate = annualInterestRate,
            interestRateType = creditSimulatorProperties.toInterestRateType()
        )

    }

    private fun calculateVariableRate(): InterestRate {
        val enabledIndex = creditSimulatorProperties.marketIndex
            .find { it.name == creditSimulatorProperties.marketIndexEnabled }

        return if (enabledIndex != null) {
            val annualInterestRate = enabledIndex.interestRate.add(enabledIndex.spread)
            InterestRate(
                spread = enabledIndex.spread,
                annualInterestRate = annualInterestRate,
                marketIndexName = creditSimulatorProperties.toMarketIndexName(),
                interestRateType = creditSimulatorProperties.toInterestRateType(),
                marketIndexAnnualInterestRate = enabledIndex.interestRate
            )
        } else {
            throw InvalidMarketIndex(creditSimulatorProperties.marketIndexEnabled.name)
        }
    }
}