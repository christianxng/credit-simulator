package br.com.creditas.credit_simulator.infrastructure.configuration

import br.com.creditas.credit_simulator.application.domain.InterestRate
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
@ConfigurationProperties(prefix = "credit.simulator")
data class CreditSimulatorProperties(
    var interestRateType: InterestRateType = InterestRateType.FIXED,
    var marketIndex: List<MarketIndex> = emptyList(),
    var marketIndexEnabled: MarketIndexName = MarketIndexName.CDI
) {

    enum class InterestRateType {
        FIXED, VARIABLE
    }

    enum class MarketIndexName {
        CDI, SELIC, IPCA
    }

    data class MarketIndex(
        var name: MarketIndexName = MarketIndexName.CDI,
        var spread: BigDecimal = BigDecimal.ZERO,
        var interestRate: BigDecimal = BigDecimal.ZERO
    )


    fun toInterestRateType() : InterestRate.InterestRateType {
        return when (this.interestRateType){
            InterestRateType.FIXED -> InterestRate.InterestRateType.FIXED
            InterestRateType.VARIABLE -> InterestRate.InterestRateType.VARIABLE
        }
    }

    fun toMarketIndexName() : InterestRate.MarketIndexName {
        return when (this.marketIndexEnabled){
            MarketIndexName.CDI -> InterestRate.MarketIndexName.CDI
            MarketIndexName.SELIC -> InterestRate.MarketIndexName.SELIC
            MarketIndexName.IPCA -> InterestRate.MarketIndexName.IPCA
        }
    }
}