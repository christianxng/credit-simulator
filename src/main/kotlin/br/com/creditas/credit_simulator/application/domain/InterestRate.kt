package br.com.creditas.credit_simulator.application.domain

import java.math.BigDecimal

class InterestRate (
    var spread: BigDecimal? = null,
    var annualInterestRate: BigDecimal,
    val interestRateType: InterestRateType,
    val marketIndexName: MarketIndexName? = null,
    var marketIndexAnnualInterestRate: BigDecimal? = null,
){
    enum class InterestRateType {
        FIXED, VARIABLE
    }

    enum class MarketIndexName {
        CDI, SELIC, IPCA
    }
}