package br.com.creditas.credit_simulator.application.ports.`in`

import java.math.BigDecimal

interface InterestRatePort {
    operator fun invoke(yearsOld: Int): BigDecimal
}